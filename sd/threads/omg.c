#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

int thread_count;

void* hello(void* id) {
    long my_id = (long) id;
    printf("Hello from thread %ld of %d\n", my_id, thread_count);
    return NULL;
}

int main(int argc, char* argv[]) {
    long thread;
    pthread_t* thread_handles;

    if(argc < 2) {
        printf("usage: %s <number of threads>", argv[0]); 
        return 1;
    }

    thread_count = strtol(argv[1], NULL, 10);
    thread_handles = malloc(thread_count*sizeof(pthread_t));

    for (thread = 0; thread < thread_count; thread++)
        pthread_create(&thread_handles[thread], NULL, hello, (void*) thread);

    for (thread = 0; thread < thread_count; thread++)
        pthread_join(thread_handles[thread], NULL);

    free(thread_handles);

    printf("Hello from the main thread\n");
    return 0;
}

