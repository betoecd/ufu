--01-Nome e cidade de clientes que possuem algum emprestimo, ordenado pelo nome do cliente
SELECT DISTINCT cliente.nome_cliente, cidade_cliente
FROM emprestimo, cliente
WHERE cliente.nome_cliente=emprestimo.nome_cliente
ORDER BY cliente.nome_cliente;


--02-Nome e cidade de clientes que possuem emprestimo na PUC, ordenado pelo nome do cliente
SELECT DISTINCT cliente.nome_cliente, cliente.cidade_cliente
FROM cliente, emprestimo
WHERE cliente.nome_cliente=emprestimo.nome_cliente
and emprestimo.nome_agencia='PUC'
ORDER BY cliente.nome_cliente;

SELECT * FROM CONTA;

--03-Nomes de Clientes com saldo entre 100 e 900
SELECT cliente.nome_cliente, sum(deposito.saldo_deposito)
FROM cliente, deposito
WHERE cliente.nome_cliente=deposito.nome_cliente
GROUP BY cliente.nome_cliente
HAVING sum(deposito.saldo_deposito) between 100 and 900;


--04-Nomes de clientes, valores de depósitos e empréstimos na agencia PUC
SELECT D.nome_cliente, D.saldo_deposito, E.valor_emprestimo, E.nome_agencia
FROM deposito AS D, emprestimo AS E
WHERE D.nome_agencia='PUC' AND
D.nome_agencia=E.nome_agencia AND
D.nome_cliente=E.nome_cliente


﻿--05-Selecione os nomes dos clientes da cidade de Contagem com depósitos maiores do que R$ 3.000,00
select cliente.nome_cliente, cliente.cidade_cliente, deposito.saldo_deposito
from cliente, deposito
where cliente.nome_cliente=deposito.nome_cliente
and cliente.cidade_cliente = 'Contagem'
and deposito.saldo_deposito > 3000;


--06-Um gerente pretende criar uma lista de clientes que correm o risco
--de se individar de maneira irreversível. Para tanto ele formulou
--a seguinte pesquisa:
--Selecione os clientes da cidade de Santa Luzia com depósitos
--menores do que R$ 1.000,00 e emprestimos maiores que R$ 1.000,00
select cliente.nome_cliente, cliente.cidade_cliente, 
       deposito.saldo_deposito, emprestimo.valor_emprestimo
from cliente, emprestimo, deposito
where cliente.nome_cliente = deposito.nome_cliente
and deposito.nome_cliente = emprestimo.nome_cliente
and cliente.cidade_cliente = 'Santa Luzia'
and deposito.saldo_deposito < 1000
and emprestimo.valor_emprestimo > 1000


--07-Um gerente pretende criar uma lista de clientes que correm o risco
--de se individar de maneira irreversível. Para tanto ele formulou
--a seguinte pesquisa:
--Selecione os clientes da cidade de Santa Luzia com uma média de 
--depósitos menor do que a média de empréstimos
select cliente.nome_cliente, avg(deposito.saldo_deposito), avg(emprestimo.valor_emprestimo)
from cliente, deposito, emprestimo
where cliente.nome_cliente = deposito.nome_cliente
and deposito.nome_cliente = emprestimo.nome_cliente
and cliente.cidade_cliente = 'Santa Luzia'
group by cliente.nome_cliente
having avg(deposito.saldo_deposito) < avg(emprestimo.valor_emprestimo)


--08-É preciso atualizar a informação do saldo do cliente na tabela cliente.
--para este propósito devemos levar em conta o saldo dos depósitos menos os
--saldos de empréstimos. o cálculo final deve ser armazenado na tabela conta.

UPDATE CONTA SET SALDO_CONTA = 0;

SELECT NOME_CLIENTE, SALDO_CONTA FROM CONTA ORDER BY SALDO_CONTA DESC;

SELECT NUMERO_CONTA, NOME_AGENCIA, NOME_CLIENTE, SUM(SALDO_DEPOSITO)
FROM DEPOSITO 
GROUP BY NUMERO_CONTA, NOME_AGENCIA, NOME_CLIENTE;


--Primeiro os clientes que possuem deposito e emprestimos (ambos)
SELECT	E.NOME_CLIENTE, E.NOME_AGENCIA, E.NUMERO_CONTA,
        SUM(D.SALDO_DEPOSITO), SUM(E.VALOR_EMPRESTIMO), 
	SUM(D.SALDO_DEPOSITO)-SUM(E.VALOR_EMPRESTIMO) AS TOTAL
FROM EMPRESTIMO AS E, DEPOSITO AS D
WHERE E.NOME_CLIENTE = D.NOME_CLIENTE
GROUP BY E.NOME_CLIENTE, E.NOME_AGENCIA, E.NUMERO_CONTA;


--Atualiza contas que possuem deposito e emprestimos (ambos)
UPDATE CONTA SET SALDO_CONTA = RELATORIO.TOTAL
FROM(
	SELECT	E.NOME_CLIENTE, E.NOME_AGENCIA, E.NUMERO_CONTA,
		SUM(D.SALDO_DEPOSITO), SUM(E.VALOR_EMPRESTIMO), 
		SUM(D.SALDO_DEPOSITO)-SUM(E.VALOR_EMPRESTIMO) AS TOTAL
	FROM EMPRESTIMO AS E, DEPOSITO AS D
	WHERE E.NOME_CLIENTE = D.NOME_CLIENTE
	GROUP BY E.NOME_CLIENTE, E.NOME_AGENCIA, E.NUMERO_CONTA
) AS RELATORIO
WHERE CONTA.NOME_CLIENTE = RELATORIO.NOME_CLIENTE
AND   CONTA.NOME_AGENCIA = RELATORIO.NOME_AGENCIA
AND   CONTA.NUMERO_CONTA = RELATORIO.NUMERO_CONTA;


-- SEGUNDO OS CLIENTES QUE POSSUEM APENAS DEṔOSITOS
SELECT  D.NOME_CLIENTE, D.NOME_AGENCIA, D.NUMERO_CONTA,
	SUM(D.SALDO_DEPOSITO) AS SALDO
FROM DEPOSITO AS D
WHERE D.NOME_CLIENTE NOT IN (
	SELECT DISTINCT E.NOME_CLIENTE
	FROM EMPRESTIMO AS E
)
GROUP BY D.NOME_CLIENTE, D.NOME_AGENCIA, D.NUMERO_CONTA;

-- ATUALIZA CONTAS QUE POSSUEM APENAS DEPOSITO
UPDATE CONTA SET SALDO_CONTA = R.SALDO
FROM (
	SELECT  D.NOME_CLIENTE, D.NOME_AGENCIA, D.NUMERO_CONTA,
		SUM(D.SALDO_DEPOSITO) AS SALDO
	FROM DEPOSITO AS D
	WHERE D.NOME_CLIENTE NOT IN (
		SELECT DISTINCT E.NOME_CLIENTE
		FROM EMPRESTIMO AS E
	)
	GROUP BY D.NOME_CLIENTE, D.NOME_AGENCIA, D.NUMERO_CONTA
) AS R
WHERE   CONTA.NOME_CLIENTE = R.NOME_CLIENTE
AND 	CONTA.NOME_AGENCIA = R.NOME_AGENCIA
AND	CONTA.NUMERO_CONTA = R.NUMERO_CONTA;


-- TERCEIRO OS CLIENTE QUE POSSUEM APENAS EMPRESTIMOS
SELECT  E.NOME_CLIENTE, E.NOME_AGENCIA, E.NUMERO_CONTA,
	-SUM(E.VALOR_EMPRESTIMO) AS DIVIDA
FROM EMPRESTIMO AS E
WHERE E.NOME_CLIENTE NOT IN (
	SELECT DISTINCT D.NOME_CLIENTE
	FROM DEPOSITO AS D
)
GROUP BY E.NOME_CLIENTE, E.NOME_AGENCIA, E.NUMERO_CONTA;

-- ATUALIZA CONTAS QUE POSSUEM APENAS EMPRESTIMO
UPDATE CONTA SET SALDO_CONTA = R.DIVIDA
FROM (
	SELECT  E.NOME_CLIENTE, E.NOME_AGENCIA, E.NUMERO_CONTA,
		-SUM(E.VALOR_EMPRESTIMO) AS DIVIDA
	FROM EMPRESTIMO AS E
	WHERE E.NOME_CLIENTE NOT IN (
		SELECT DISTINCT D.NOME_CLIENTE
		FROM DEPOSITO AS D
	)
	GROUP BY E.NOME_CLIENTE, E.NOME_AGENCIA, E.NUMERO_CONTA
) AS R
WHERE   CONTA.NOME_CLIENTE = R.NOME_CLIENTE
AND 	CONTA.NOME_AGENCIA = R.NOME_AGENCIA
AND	CONTA.NUMERO_CONTA = R.NUMERO_CONTA;
