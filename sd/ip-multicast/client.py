import socket
import time

MCAST_GRP = '224.1.1.1'
MCAST_PORT = 5007

sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
sock.setsockopt(socket.IPPROTO_IP, socket.IP_MULTICAST_TTL, 2)
count = 0.0

client_name = input()
while True:
    time.sleep(.5)
    count = count + .1
    sock.sendto((client_name + str(count)).encode(), (MCAST_GRP, MCAST_PORT))