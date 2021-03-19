#client.py

#!/usr/bin/python                               # This is client.py file

import socket                                   # Import socket module

s = socket.socket()                             # Create a socket object
host = socket.gethostname()                # Get local machine name
port = 12345                                    # Reserve a port for your service.

s.connect((host, port))

while True:
    print('Digite sua mensagem: ')
    msg = input()

    if msg == '':
        print('Insira uma mensagem válida!')
    elif msg == 'SAIR':
        s.send(msg.encode())
        break
    else:
        s.send(msg.encode())
        response = s.recv(1024)
        print(response.decode())

s.close() 