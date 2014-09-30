open(FILE, "<rawDataFINAL2013.txt") || die "input";
open(OUT, ">dataToSQL.txt") || die "output";

print OUT "DROP TABLE IF EXISTS ducks;\n";
print OUT "CREATE TABLE ducks (species TEXT, county TEXT, location TEXT, latitude REAL, longitude REAL, month INT);\n";

while(<FILE>) {
	
	@line = (split /\t/, $_);
	chomp(@line[5]);
	@date = (split /-/, @line[5]);
	
	if((@line[1] eq "Jackson" || @line[1] eq "Lenawee" || @line[1] eq "Monroe" || @line[1] eq "Wayne" || @line[1] eq "Livingston" || @line[1] eq "Ingham" || @line[1] eq "Washtenaw")){
		
		#print "Match\n";

		@line[2] =~ s/'//;
		@line[0] =~ s/'s//;

		#print "@line[2]\n";

		if(@date[1] eq "01"){
			@line[5] = 1;
		} elsif(@date[1] eq "02"){
			@line[5] = 2;
		} elsif(@date[1] eq "03"){
			@line[5] = 3;
		} elsif(@date[1] eq "04"){
			@line[5] = 4;
		} elsif(@date[1] eq "05"){
			@line[5] = 5;
		} elsif(@date[1] eq "06"){
			@line[5] = 6;
		}  elsif(@date[1] eq "07"){
			@line[5] = 7;
		} elsif(@date[1] eq "08"){
			@line[5] = 8;
		} elsif(@date[1] eq "09"){
			@line[5] = 9;
		} elsif(@date[1] eq "10") {
			@line[5] = 10;
		} elsif(@date[1] eq "11"){
			@line[5] = 11;
		} elsif(@date[1] eq "12"){
			@line[5] = 12;
		} else {
			print "@line[5]\n";
			@line[5] = "UNKNOWN DATE";
		}
		print OUT "INSERT INTO ducks VALUES('@line[0]', '@line[1]', '@line[2]', @line[3], @line[4], @line[5]);\n";
	}

}

close FILE;
close OUT;
