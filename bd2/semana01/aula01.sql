select distinct nome_agencia from deposito;


select distinct nome_cliente from deposito
intersect
select distinct nome_cliente from emprestimo;


select distinct nome_cliente from deposito where nome_agencia='PUC'
intersect
select distinct nome_cliente from emprestimo where nome_agencia='PUC';


select distinct nome_cliente from (
	select distinct nome_cliente, nome_agencia from deposito
	intersect
	select distinct nome_cliente, nome_agencia from emprestimo
) as relatorio where relatorio.nome_agencia='PUC';


SELECT DISTINCT nome_cliente FROM deposito WHERE nome_agencia='PUC'
AND nome_cliente IN (SELECT DISTINCT nome_cliente FROM emprestimo WHERE nome_agencia='PUC');


SELECT DISTINCT nome_cliente FROM deposito WHERE nome_agencia='PUC'
EXCEPT
SELECT DISTINCT nome_cliente FROM emprestimo WHERE nome_agencia='PUC';


SELECT DISTINCT nome_cliente FROM deposito WHERE nome_agencia='PUC'
AND nome_cliente NOT IN (SELECT DISTINCT nome_cliente FROM emprestimo WHERE nome_agencia='PUC');


SELECT DISTINCT nome_cliente FROM deposito WHERE nome_agencia='PUC'
UNION
SELECT DISTINCT nome_cliente FROM emprestimo WHERE nome_agencia='PUC';


SELECT nome_cliente FROM cliente WHERE cidade_cliente='Belo Horizonte';


SELECT nome_cliente FROM cliente WHERE nome_cliente LIKE '%Santos%';


SELECT nome_cliente FROM cliente WHERE nome_cliente LIKE '%Sou_a%';


SELECT nome_cliente, SUM(saldo_deposito)
FROM deposito GROUP BY nome_cliente;


SELECT nome_cliente, saldo_deposito
FROM deposito
WHERE saldo_deposito >= 3000 AND saldo_deposito <= 4000;


SELECT nome_cliente, saldo_deposito
FROM deposito
WHERE saldo_deposito BETWEEN 3000 AND 4000;


SELECT nome_cliente, SUM(saldo_deposito)
FROM deposito GROUP BY nome_cliente
HAVING SUM(saldo_deposito) > 5000;


SELECT nome_cliente, SUM(saldo_deposito)
FROM deposito GROUP BY nome_cliente 
HAVING SUM(saldo_deposito) BETWEEN 1000 AND 4000
ORDER BY SUM(saldo_deposito);


SELECT nome_cliente, SUM(saldo_deposito)
FROM deposito GROUP BY nome_cliente 
HAVING SUM(saldo_deposito) > AVG(saldo_deposito)
ORDER BY SUM(saldo_deposito);


SELECT nome_agencia, nome_cliente, SUM(saldo_deposito)
FROM deposito
GROUP BY nome_agencia, nome_cliente
ORDER BY nome_cliente, nome_agencia;


SELECT nome_cliente, nome_agencia, SUM(saldo_deposito)
FROM deposito
GROUP BY nome_cliente, nome_agencia
HAVING SUM(saldo_deposito) > ALL (
	SELECT SUM(saldo_deposito) FROM deposito
	WHERE nome_agencia='Pampulha' GROUP BY nome_agencia);










