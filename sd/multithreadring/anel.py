#!/usr/bin/python3

from threading import Thread
from time import sleep
import sys


class logger:
    def __init__(self) -> None:
        self.HEADER = '\033[95m'
        self.OKBLUE = '\033[94m'
        self.OKCYAN = '\033[96m'
        self.OKGREEN = '\033[92m'
        self.WARNING = '\033[93m'
        self.FAIL = '\033[91m'
        self.ENDC = '\033[0m'
        self.BOLD = '\033[1m'
        self.UNDERLINE = '\033[4m'

    def title(self, message):
        print(f'{self.HEADER}{self.BOLD}{self.UNDERLINE}{message}{self.ENDC}')

    def warning(self, message):
        print(f'{self.WARNING}{message}{self.ENDC}')

    def success(self, message):
        print(f'{self.OKGREEN}{message}{self.ENDC}')

    def red(self, message):
        print(f'{self.FAIL}{message}{self.ENDC}')

    def info(self, message):
        print(message)


log = logger()


class MultithreadRing(Thread):

    def __init__(self, thread_id: int) -> bool:
        Thread.__init__(self)

        # initial state
        self.thread_id = thread_id
        self.sleep = True
        self.complete = False
        self.message = None
        self.next_thread = None

    def run(self):
        log.warning(f'thread {self.thread_id}: \tStarting...')

        while not self.complete:
            if self.sleep:
                sleep(.3)
                continue

            for i, character in enumerate(self.message):
                if character.islower():
                    self.message = self.message[:i] + character.upper() + self.message[i+1:]
                    log.success(f'thread {self.thread_id}:')
                    log.info(f'\t{self.message}')
                    break
            if self.message.isupper():
                self.complete = True
                log.success(f'thread {self.thread_id}: Message translate')
            else:
                self.sleep = True
                self.send_message(self.message)

        self.next_thread.complete = True
        log.red(f'thread {self.thread_id}: \t Finished!')

    def send_message(self, message):
        self.next_thread.message = message
        self.next_thread.sleep = False


def main(message: str):
    log.title('\n\n\t\tMultithreadRing!!!\n\n')
    threads = [ MultithreadRing(thread_id) for thread_id in range(1, 31) ]
    for i, thread in enumerate(threads):
        if not i == 29:
            thread.next_thread = threads[i+1]
        else:
            thread.next_thread = threads[0]
        thread.start()
        
    threads[0].send_message(message)

    for thread in threads:
        thread.join()


if __name__ == "__main__":
    assert len(sys.argv) == 2, "usage: %s <message>" % sys.argv[0]
    message = sys.argv[1]
    exit(main(message))
        