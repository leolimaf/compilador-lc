sseg SEGMENT STACK ;início seg. pilha
byte 4000h DUP(?) ;dimensiona pilha
sseg ENDS ;fim seg. pilha
dseg SEGMENT PUBLIC ;início seg. dados
byte 4000h DUP(?) ;temporários
dseg ENDS ;fim seg. dados
cseg SEGMENT PUBLIC ;início seg. código
ASSUME CS:cseg, DS:dseg
strt:
mov ax, dseg
mov ds, ax
dseg SEGMENT PUBLIC
byte "Hello World!$"
dseg ENDS
; "Hello World!" em 16384
mov dx, 0
mov ah, 09h
int 21h
mov ah, 02h
mov dl, 0Dh
int 21h
mov DL, 0Ah
int 21h
mov ah, 4Ch
int 21h
cseg ENDS ;fim seg. código
END strt ;fim programa
