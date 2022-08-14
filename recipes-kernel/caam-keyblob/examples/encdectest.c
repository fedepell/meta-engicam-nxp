#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/ioctl.h>


// CAAM Keyblob driver (https://github.com/usbarmory/caam-keyblob) example program
// Warning: this is not production code, just an example :-)

#define CAAM_RAM_SIZE 65535
#define KEY_MODIFIER_SIZE 16

#define CAAM_KB_ENCRYPT 0xc0304900
#define CAAM_KB_DECRYPT 0xc0304901

struct caam_kb_data {
    char *rawkey;
    size_t rawkey_len;
    char *keyblob;
    size_t keyblob_len;
    char *keymod;
    size_t keymod_len;
};


int main() {
    int fd;
    struct caam_kb_data camdata = {};
    char input[CAAM_RAM_SIZE] = { 0 } ;
    char output[CAAM_RAM_SIZE] = { 0 };
    char keym[KEY_MODIFIER_SIZE] = { 0 };

    fd = open("/dev/caam_kb", O_RDWR);
    if(fd < 0) {
            printf("Cannot open /dev/kb\n");
            return 0;
    }

    sprintf(input, "We will encrypt this line!\n");
    camdata.rawkey = input;
    camdata.rawkey_len = strlen(input);

    camdata.keyblob = output;
    camdata.keyblob_len = camdata.rawkey_len + 32 + KEY_MODIFIER_SIZE;

    // Key modifier is not used in this example
    camdata.keymod = keym;
    camdata.keymod_len = 16;


    printf("Encrypting: [%s]\n", camdata.rawkey);
    ioctl(fd, CAAM_KB_ENCRYPT, (struct caam_kb_data *) &camdata);


    // Remove cleantext
    memset(input, 0x0, 65535);
    camdata.rawkey_len = camdata.keyblob_len - 32 - 16;


    printf("Decrypting: [%s]\n", camdata.keyblob);
    ioctl(fd, CAAM_KB_DECRYPT, (struct caam_kb_data *) &camdata);
    printf("Decrypted to: %s\n", camdata.rawkey);

    close(fd);
}
