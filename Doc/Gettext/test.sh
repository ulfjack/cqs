xgettext --from-code=UTF-8 -c -L C -o test.pot test.c
msginit -i test.pot -l de_DE
msgmerge -U de.po test.pot

