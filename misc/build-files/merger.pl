#!/usr/bin/perl -w
use warnings;
use strict;

if($#ARGV < 0) {
	die("Useage: merger.pl list [list2 [...]]");
}

foreach (@ARGV) {
	open(LISTFIL, "<$_") || die("Cannot open $_ for input");
	while(<LISTFIL>) {
		chomp;
		open(OUTFIL, ">$_") || die("Cannot open $_ for output");
		print "Overwriting $_\n";
		while(<LISTFIL>) {
			if(/^\s*$/) {
				last;
			}
			chomp;
			open(INFIL, "<$_") || die("Cannot open $_ for input");
			while(<INFIL>) {
				print OUTFIL;
			}
			close(INFIL);
		}
		close(OUTFIL);
	}
	close(LISTFIL);
}
