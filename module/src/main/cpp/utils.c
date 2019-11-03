#include "utils.h"

char *malloc_and_load_file(const char *path) {
    if ( path == NULL )
        return NULL;

    if ( access(path, R_OK) != 0 )
        return NULL;

    int fd = open(path, O_RDONLY);
    if ( fd < 0 )
        return NULL;

    struct stat file_stat;
    if ( fstat(fd, &file_stat) < 0 ) {
        close(fd);
        return NULL;
    }

    char *result = (char*) malloc((size_t) file_stat.st_size);

    read(fd, result, (size_t) file_stat.st_size);

    close(fd);

    return result;
}
