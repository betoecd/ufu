--IMPLEMENTE UM GATILHO (TRIGGER) QUE ATUALIZE A TABELA CONTA, PARA O CAMPO SALDO_CONTA, 
--SEMPRE QUE UMA NOVA LINHA FOR INSERIDA NA TABELA DE DEPÓSITO OU EMPRÉSTIMO.

CREATE OR REPLACE FUNCTION atualiza_saldo_conta()
	RETURNS TRIGGER AS
$BODY$
DECLARE
	local_total_deposito FLOAT;
	local_total_emprestimo FLOAT;
	local_numero_conta_deposito INTEGER;
	local_numero_conta_emprestimo INTEGER;
	local_nome_agencia_deposito CHARACTER VARYING;
	local_nome_agencia_emprestimo CHARACTER VARYING;
	cursor_deposito CURSOR FOR SELECT C.NUMERO_CONTA, C.NOME_AGENCIA,
	COALESCE(SUM(D.SALDO_DEPOSITO),0) AS TOTAL_DEPOSITO
	FROM CONTA AS C NATURAL LEFT OUTER JOIN DEPOSITO AS D
	GROUP BY C.NUMERO_CONTA, C.NOME_AGENCIA
	ORDER BY C.NUMERO_CONTA;
	cursor_emprestimo CURSOR FOR SELECT C.NUMERO_CONTA, C.NOME_AGENCIA,
	COALESCE(SUM(E.VALOR_EMPRESTIMO),0) AS TOTAL_EMPRESTIMO
	FROM CONTA AS C NATURAL LEFT OUTER JOIN EMPRESTIMO AS E
	GROUP BY C.NUMERO_CONTA, C.NOME_AGENCIA
	ORDER BY C.NUMERO_CONTA;
BEGIN
	RAISE NOTICE 'GALVÃO! FALA TINO! SENTIU!';
	OPEN cursor_deposito;
	LOOP
		FETCH cursor_deposito INTO local_numero_conta_deposito, local_nome_agencia_deposito, local_total_deposito;
		IF FOUND THEN
			FOR emprestiminho in cursor_emprestimo
			LOOP
				IF local_numero_conta_deposito=emprestiminho.NUMERO_CONTA
				AND local_nome_agencia_deposito=emprestiminho.NOME_AGENCIA THEN
					UPDATE CONTA SET SALDO_CONTA = local_total_deposito - emprestiminho.TOTAL_EMPRESTIMO
					WHERE NUMERO_CONTA = local_numero_conta_deposito
					AND NOME_AGENCIA = local_nome_agencia_deposito;
				EXIT;
				END IF;
			END LOOP;
		END IF;
		IF NOT FOUND THEN EXIT; END IF;
	END LOOP;
	CLOSE cursor_deposito;
	RETURN NULL;
END
$BODY$
LANGUAGE plpgsql VOLATILE COST 100;
ALTER FUNCTION atualiza_saldo_conta() OWNER TO aluno;



CREATE TRIGGER TRIGGER_atualiza_saldo_conta_from_deposito
AFTER UPDATE ON DEPOSITO
FOR EACH STATEMENT EXECUTE PROCEDURE atualiza_saldo_conta();

CREATE TRIGGER TRIGGER_atualiza_saldo_conta_from_deposito_insert
AFTER INSERT ON DEPOSITO
FOR EACH STATEMENT EXECUTE PROCEDURE atualiza_saldo_conta();

CREATE TRIGGER TRIGGER_atualiza_saldo_conta_from_emprestimo
AFTER UPDATE ON EMPRESTIMO
FOR EACH STATEMENT EXECUTE PROCEDURE atualiza_saldo_conta();

CREATE TRIGGER TRIGGER_atualiza_saldo_conta_from_emprestimo_insert
AFTER INSERT ON EMPRESTIMO
FOR EACH STATEMENT EXECUTE PROCEDURE atualiza_saldo_conta();
