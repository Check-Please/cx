#!/usr/bin/perl -w
use warnings;
use strict;

if($#ARGV != 2) {
	die("Useage: compile-templates.pl src java_dest js_dest");
}

opendir(SRCDIR, "$ARGV[0]") || die("Cannot open $ARGV[0] for input");

my $package = $ARGV[1];
$package =~ s/[\\\/]/\./g;
$package =~ s/.*[\.^]src\.(.*)/$1/;

while (my $f = readdir(SRCDIR)) {
	if($f =~ m/^[a-zA-Z0-9_]+\.tmplt$/) {

		my $fname = substr $f, 0, (length $f) - 6;

		open(SFIL, "<$ARGV[0]/$fname.tspec") ||
				die("Can't open $fname.tspec for input");
		my @params = ();
		my @javaParams = ();
		while(<SFIL>) {
			chomp;
			my $type = "Object";
			if(m/^\s*\@param\s+[a-zA-Z0-9_]+\s+\{[a-zA-Z0-9_]+\}/) {
				$type = $_;
				$type =~ s/^\s*\@param\s+[a-zA-Z0-9_]+\s+\{([a-zA-Z0-9_]+)\}.*$/$1/;
			}
			if(s/^\s*\@param\s+([a-zA-Z0-9_]+).*$/$1/) {
				push @params, $_;
				push @javaParams, "$type $_";
			}
		}
		close(SFIL);

		my $content = "";
		open(TFIL, "<$ARGV[0]/$f") || die("Cannot open $f for input");
		{
			local $/;
			$_ = <TFIL>;
			s/\s*$//;
			my @tokens = split /(?:\{\{|\}\})/;
			for(my $i = 0; $i <= $#tokens; $i++) {
				if($i % 2 == 0) {
					$tokens[$i] =~ s/\\/\\\\/g;
					$tokens[$i] =~ s/\t/\\t/g;
					$tokens[$i] =~ s/\r/\\r/g;
					$tokens[$i] =~ s/\f/\\f/g;
					$tokens[$i] =~ s/\'/\\\'/g;
					$tokens[$i] =~ s/\"/\\\"/g;
					$tokens[$i] =~ s/\n/\\n\"\+\n\t\t\t\"/g;
					$content = "$content$tokens[$i]";
				} else {
					$tokens[$i] =~ s/^\s*//;
					$tokens[$i] =~ s/\s*$//;
					if($tokens[$i] =~ m/\n/) {
						$tokens[$i] =~ s/\s+/ /g;
						$content =
							"$content\"+\n\t\t\t\t($tokens[$i])+\n\t\t\t\"";
					} else {
						$content = "$content\"+($tokens[$i])+\"";
					}
				}
			}
		}
		close(TFIL);

		open(OUTFIL, ">$ARGV[2]/$fname.js") ||
				die("Cannot open $ARGV[2]/$fname.js for output");
		print OUTFIL "var template = template || {};\n\n" .
				"template.$fname = function(" .
					(join ", ", @params) . ") {\n" .
				"\treturn\t\"$content\";\n};";
		close(OUTFIL);

		$fname = (ucfirst $fname) . "Template";
		$content =~ s/\t\t\t/\t\t\t\t/g;
		open(OUTFIL, ">$ARGV[1]/$fname.java") ||
				die("Cannot open $ARGV[1]/$fname.java for output");
		print OUTFIL "package $package;\n\n" .
				"public class $fname {\n" .
				"\tpublic static String run(".
					(join ", ", @javaParams).") {\n" .
				"\t\treturn\t\"$content\";\n\t}\n}";
		close(OUTFIL);
	}
}

closedir(SRCDIR);
