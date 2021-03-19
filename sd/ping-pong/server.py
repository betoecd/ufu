#server.py
#!/usr/bin/python                               # This is server.py file

import socket                                   # Import socket module

s = socket.socket()                             # Create a socket object
host = socket.gethostname()                       # Get local machine name
port = 12345                                    # Reserve a port for your service.
s.bind((host, port))                            # Bind to the port

s.listen(5)                                     # Now wait for client connections.

c, addr = s.accept()                         # Establish connection with client.
print('Got connection from', addr)
while True:
    cliente_msg = c.recv(1024).decode()
    print(cliente_msg)
    
    if cliente_msg == 'lalala':
        c.send('lelele'.encode())
    elif cliente_msg == 'SAIR':
        break
    else:
        c.send('...'.encode())

c.close() 