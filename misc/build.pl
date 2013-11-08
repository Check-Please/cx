#!/usr/bin/perl -w
use warnings;
use strict;

my $sep = "/";
my $rmdir = "rm -r";
if($^O =~ m/MSWin/) {
	$sep = "\\";
	$rmdir = "rmdir /Q /S";
}


system("sass --update scss:css");
system("perl build-files$sep"."merger.pl build-files$sep"."css-merge-list build-files$sep"."js-merge-list build-files$sep"."jquery-merge-list");
system("perl build-files$sep"."compile-templates.pl templates ..$sep"."src$sep"."templates js$sep"."templates");
system("perl build-files$sep"."make-web-xml.pl build-files$sep"."servlet-list.csv WEB-INF$sep"."web.xml");
