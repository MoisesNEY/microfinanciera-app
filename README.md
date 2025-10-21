"# microfinanciera-app" 

## COMANDOS PARA CONECTARME A CADA BASE DE DATOS
docker exec -it mf_postgres_main psql -U root -d ms_loan
docker exec -it mf_postgres_main psql -U root -d postgres
docker exec -it mf_postgres_main psql -U root -d ms_payment
docker exec -it mf_postgres_main psql -U root -d ms_workers
docker exec -it mf_postgres_main psql -U root -d ms_accounting
docker exec -it mf_postgres_main psql -U root -d ms_customer

docker-compose up


docker exec -it mf_postgres_main psql -U root -d postgres
postgres=# \c ms_customer
ms_customer=# SELECT id, client_code, first_name, is_active FROM clients;
                  id                  | client_code |   first_name   | is_active
--------------------------------------+-------------+----------------+-----------
 123e4567-e89b-12d3-a456-426614174000 | CLI-001     | Juan           | t
 3723e0e3-dfee-472a-8219-90a9513e9040 | CLI0038     | Moises Alberto | f
 7dfa8dc5-081e-41b1-8a2e-f15ff51b5ce9 | CLI0079     | Roger Lopez    | f
 e80dac13-1b4a-49d2-8d0d-12604f8f2350 | CLI0030     | Eduardo        | f
 b0a377a6-5d83-474d-adf6-c408cd1a6cc1 | CLI003      | oswaldo Javier | f
(5 rows)

\d workers

