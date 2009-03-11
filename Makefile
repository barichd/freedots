DATESTAMP=$(shell date +%Y%m%d)
WIN32FILE=FreeDots-$(DATESTAMP).exe
JARFILE=java/dist/FreeDots-$(DATESTAMP).jar
GC_PASSWORD=$(shell head -8 $$(grep "Google Code" ~/.subversion/auth/svn.simple/*|awk -F: '{print $$1}')|tail -1)

all: program documentation installer

program:
	cd java && ant

documentation: doc/manual.xml
	xsltproc doc/html.xsl $< > doc/manual.html
	xsltproc doc/html.xsl $< | lynx -nonumbers -dump -stdin >doc/manual.txt
	xsltproc --stringparam man.output.base.dir 'doc/' \
	         --stringparam man.output.in.separate.dir 1 \
	/usr/share/xml/docbook/stylesheet/nwalsh/manpages/docbook.xsl $<

installer: $(JARFILE)
	makensis -DJARFILE=$< -DOUTFILE=$(WIN32FILE) WindowsInstaller.nsi

distribute:
	@python googlecode_upload.py -s 'Java 1.6 JAR' -p freedots -u mlang@tugraz.at -w $(GC_PASSWORD) $(JARFILE)
	@python googlecode_upload.py -s 'MS Windows installer' -p freedots -u mlang@tugraz.at -w $(GC_PASSWORD) $(WIN32FILE)

clean:
	cd java && ant clean
	rm -f doc/manual.txt doc/man1/freedots.1
	rm -f FreeDots-*.exe

