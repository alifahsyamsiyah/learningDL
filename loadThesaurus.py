#! /usr/bin/env python
import csv

#UMLSPATH = '/users/lat/felix/bin/metathesaurus/2012AA/META/'
UMLSPATH = 'D:/EMCL/Semester1/Project/CODE/' 

# Load Metathesaurus into dictionary
def umls2snomed():
    print 'Loading Metathesaurus'
    sno_dic = {}
    try:
        with open(UMLSPATH + 'MRCONSO.RRF', 'rb') as f:
            reader = csv.reader(f, delimiter = '|')
            for line in reader:
                if line[11:13] == ['SNOMEDCT', 'FN']:
                    sno_dic[line[0]] = int(line[9])
    except IOError:
        print 'Error opening metathesaurus'
        quit()
    return sno_dic

# Load Metathesaurus into dictionary
def snomed2names():
    print 'Loading Metathesaurus'
    sno_dic = {}
    try:
        with open(UMLSPATH + 'MRCONSO.RRF', 'rb') as f:
            reader = csv.reader(f, delimiter = '|')
            for line in reader:
                if line[11:13] == ['SNOMEDCT', 'FN']:
                    sno_dic[int(line[9])] = line[14]
    except IOError:
        print 'Error opening metathesaurus'
        quit()
    return sno_dic

def snomednames2id():
    print 'Loading Metathesaurus'
    sno_dic = {}
    try:
        with open(UMLSPATH + 'MRCONSO.RRF', 'rb') as f:
            reader = csv.reader(f, delimiter = '|')
            for line in reader:
                if line[11:13] == ['SNOMEDCT', 'FN']:
                    sno_dic[line[14]] = line[9] 
    except IOError:
        print 'Error opening metathesaurus'
        quit()
    return sno_dic

if __name__=="__main__":
	fout = open("/users/lat/mayue/implementation/PubMed/Experiments/CommonDataForAllExperiments/id_list.csv",'w')
	dic = snomed2names()
	for key,value in dic.items():
		fout.write(str(key)+','+value+'\n')
	fout.close()
