#!/usr/bin/perl -w
use warnings;
use strict;

if($#ARGV != 1) {
	die("Useage: make-web-xml.pl src dest");
}

open(INFIL, "<$ARGV[0]") || die("Cannot open $ARGV[0] for input");
open(OUTFIL, ">$ARGV[1]") || die("Cannot open $ARGV[1] for output");

print OUTFIL	"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><web-app xmlns=\"http://java.sun.com/xml/ns/javaee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"2.5\" xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee                http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd\">\n" .
				"\n" .
				"\t<!-- Custom MIME types -->\n" .
				"\t<mime-mapping>\n" .
				"\t\t<extension>woff</extension>\n" .
				"\t\t<mime-type>font/opentype</mime-type>\n" .
				"\t</mime-mapping>\n" .
				"\n" .
				"\t<!-- Default page to serve -->\n" .
				"\t<welcome-file-list>\n" .
				"\t\t<welcome-file>pay</welcome-file>\n" .
				"\t</welcome-file-list>\n" .
				"\n" .
				"\t<!-- Google Endpoint API Stuff -->\n" .
				"\t<servlet>\n" .
				"\t\t<servlet-name>SystemServiceServlet</servlet-name>\n" .
				"\t\t<servlet-class>com.google.api.server.spi.SystemServiceServlet</servlet-class>\n" .
				"\t\t<init-param>\n" .
				"\t\t\t<param-name>services</param-name>\n" .
				"\t\t\t<param-value/>\n" .
				"\t\t</init-param>\n" .
				"\t</servlet>\n" .
				"\t<servlet-mapping>\n" .
				"\t\t<servlet-name>SystemServiceServlet</servlet-name>\n" .
				"\t\t<url-pattern>/_ah/spi/*</url-pattern>\n" .
				"\t</servlet-mapping>\n" .
				"\n" .
				"\t<!-- - - - - - - - - - - - -->\n" .
				"\t<!--        Servlets       -->\n" .
				"\t<!-- - - - - - - - - - - - -->\n\n";

my $prefix = "servlets";
while(my $line = <INFIL>) {
	if($line =~ m/,/) {
		my @elems = split ',', $line;
		my $file = $elems[0];
		if($file =~ m/^\./) {
			$file = $prefix . $file;
		}
		my $url = $elems[1];
		$url =~ s/^\s*(.*?)\/?\s*$/$1/;
		my $isJSP = $file =~ m/jsp$/i;
		my $tagName = $isJSP ? "jsp-file" : "servlet-class";
		my $servlet;
		if($#elems < 2) {
			$servlet = $file;
			if($isJSP) {
				$servlet =~ s/^.*\/(.*)\.jsp$/$1Servlet/;
			} else {
				$servlet =~ s/^.*\.(.*)$/$1/;
			}
			$servlet = lcfirst $servlet;
		} else {
			$servlet = $elems[2];
			$servlet =~ s/^\s*(.*?)\s*$/$1/;
		}
		print OUTFIL	"\t<servlet>\n" .
						"\t\t<servlet-name>$servlet</servlet-name>\n" .
						"\t\t<$tagName>$file</$tagName>\n" .
						"\t</servlet>\n" .
						"\t<servlet-mapping>\n" .
						"\t\t<servlet-name>$servlet</servlet-name>\n" .
						"\t\t<url-pattern>$url</url-pattern>\n" .
						"\t</servlet-mapping>\n" .
						"\t<servlet-mapping>\n" .
						"\t\t<servlet-name>$servlet</servlet-name>\n" .
						"\t\t<url-pattern>$url/</url-pattern>\n" .
						"\t</servlet-mapping>\n";
	} else {
		chomp $line;
		if(length $line > 0) {
			print OUTFIL "\n\t<!-- $line -->\n";
			$prefix = "servlets." . (lc $line);
			$prefix =~ s/\s*-\s+/\./g;
			$prefix =~ s/ /_/g;
		}
	}
}
print OUTFIL "</web-app>";
close(OUTFIL);
close(INFIL);
