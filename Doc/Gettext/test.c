#include <libintl.h>
#include <stdio.h>

int main(int argc, char** argv)
{
	// Some comment regarding the translation of the Apollo 13 scenario string.
	printf(gettext ("Apollo 13 scenario: Main thruster failed!\n"));
}
