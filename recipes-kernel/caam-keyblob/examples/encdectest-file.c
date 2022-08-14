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

// The following come from the driver
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


int main(int argc, char *argv[]) {
    int err = 0;
    int fd = -1;
    struct caam_kb_data camdata = {};
    char input[CAAM_RAM_SIZE] = { 0 };
    char output[CAAM_RAM_SIZE] = { 0 };
    char keym[KEY_MODIFIER_SIZE] = { 0 };

    if (argc < 3) {
       printf("Usage: %s <mode> <infile> <outfile>\n", argv[0]);
       printf("   mode    : 0 = encrypt, 1 = decrypt\n");
       printf("   infile  : input file (plain if mode is encrypt, encrypted if mode is decrypt)\n");
       printf("   outfile : output file with encrypted or decrypted results, depending on mode\n\n\n");
       printf("Warning: maximum input file size is ~64Kb (secure ram size)\n\n");
       return -1;
    }

    fd = open("/dev/caam_kb", O_RDWR);
    if(fd < 0) {
        printf("Cannot open /dev/caam_kb\n");
        return -1;
    }

    // Key modifiers are not user in this demo, but one could easily add support by filling the data here
    camdata.keymod = keym;
    camdata.keymod_len = KEY_MODIFIER_SIZE;

    camdata.rawkey = input;
    camdata.keyblob = output;


    FILE *rfile = fopen(argv[2], "rb");
    if (!rfile) {
        printf("Cannot open input file!\n");
    } else {
        if (atoi(argv[1]) == 0) {
           // Encrypt
           int i = fread(input, 1, CAAM_RAM_SIZE - (32 + KEY_MODIFIER_SIZE), rfile);

           printf("Will encrypt %d bytes\n", i);
           if (i == (CAAM_RAM_SIZE - (32 + KEY_MODIFIER_SIZE))) {
               printf("WARNING: file too big or just at the limit of secure ram size?\n");
           }

           camdata.rawkey_len = i; // Len of input
           camdata.keyblob_len = camdata.rawkey_len + (32 + KEY_MODIFIER_SIZE); // Output len is calculated by input

           err = ioctl(fd, CAAM_KB_ENCRYPT, (struct caam_kb_data *) &camdata);
           if (!err) {
               FILE *wfile = fopen(argv[3], "wb");
               if (wfile) {
                   fwrite(output, camdata.keyblob_len, 1, wfile);
                   fclose(wfile);
               } else {
                   printf("Cannot open output file!\n");
                   err = -2;
               }
           } else {
               printf("ioctl error %d!\n", err);
           }

        } else if (atoi(argv[1]) == 1) {
           // Decrypt
           int i = fread(output, 1, CAAM_RAM_SIZE, rfile);
           printf("Will decrypt %d bytes\n", i);

           camdata.keyblob_len = i;  // Input is in this case the blob
           camdata.rawkey_len = camdata.keyblob_len - (32 + KEY_MODIFIER_SIZE);  // Output len is calculated by input

           err = ioctl(fd, CAAM_KB_DECRYPT, (struct caam_kb_data *) &camdata);

           if (!err) {
               FILE *wfile = fopen(argv[3], "wb");
               if (wfile) {
                   fwrite(input, camdata.rawkey_len, 1, wfile);
                   fclose(wfile);
               } else {
                   printf("Cannot open output file!\n");
                   err = -2;
               }
           } else {
               printf("ioctl error %d!\n", err);
           }

        } else {
            printf("Unknown mode %d\n", atoi(argv[1]));
            err = -1;
        }

        fclose(rfile);
    }

    close(fd);
    return err;
}
