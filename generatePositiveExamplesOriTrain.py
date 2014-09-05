import csv
import re
from glob import glob
from loadDictionaries import snomed2names

# ROLEPATH = '/users/lat/felix/Dropbox/Work/Hybris/Snomed2012Jan_RolePairs_Explicit/'
#ROLEPATH = '../data/rolepairs/'
#ROLEPATH   = '/users/lat/mayue/implementation/PubMed/Experiments/CommonDataForAllExperiments/3RelationBase/COMPLETE/'
ROLEPATH = 'D:/EMCL/Semester1/Project/CODEFINAL/'
AM_FILE = ROLEPATH + 'Associated morphology (attribute).csv'
CA_FILE = ROLEPATH + 'Causative agent (attribute).csv'
FS_FILE = ROLEPATH + 'Finding site (attribute).csv'
IS_FILE = ROLEPATH + 'dirISA.csv'

#PREPPATH = "/users/lat/mayue/implementation/PubMed/Experiments/Exp0_D4D_withoutMakefile/work1/STFDClassification/testdata/"
TRAIPATH = "D:/EMCL/Semester1/Project/CODEFINAL/"
#ANNOPATH = "/users/lat/mayue/implementation/PubMed/Experiments/CommonDataForAllExperiments/Felix/WikipediaAnnotated/"
ANNOPATH = "D:/EMCL/Semester1/Project/CODEFINAL/WikipediaAnnotated/"
#TMPPATH = ANNOPATH

PREPOSITIONS = set(['about', 'above', 'across', 'after', 'against', 'along',
    'alongside', 'amid', 'among', 'around', 'as', 'at', 'atop', 'before',
    'behind', 'below', 'beneath', 'beside', 'besides', 'between', 'beyond',
    'by', 'despite', 'due','during', 'except', 'for', 'from', 'in', 'into', 'like',
    'near', 'of', 'off', 'on', 'onto', 'out', 'over', 'past', 'than',
    'through', 'throughout', 'to', 'toward', 'towards', 'under', 'underneath',
    'unlike', 'until', 'up', 'upon', 'via', 'with', 'within', 'without'])



def extract_type(concept_name):
    match = re.match('.* \(([^\(\)]*)\)$', concept_name)
    #print match.group(1)
    return match.group(1)

def type_from_id(i):
    try: 
        return extract_type(NAMES_DIC[i])
    except:
        return "Null-Type"

def get_sentence(reader):
    try:
        line = reader.next()

        phrase_list= []
        id_list= []
        
        while line != []:
            phrase_list.append(line[0])
            id_list.append(map(int, line[1:]))
            line = reader.next()

        return phrase_list, id_list
    except StopIteration:
        return None


def read_relation(filename):
    relation = set([])
    reader = csv.reader(open(filename, 'r'))
    for line in reader:
        relation.add(tuple(map(int, line)))

    return relation

def get_strings(j, k, phrase_list):  
    if j-1 >= 0:
        lw = phrase_list[j - 1]
    else:
        lw = ''
    
    if k+1 < len(phrase_list):
        rw = phrase_list[k + 1]
    else:
        rw = ''

    bw = ' '.join(phrase_list[j + 1:k])

    first = re.match('\S*', phrase_list[j]).group(0)
    if first in PREPOSITIONS:
        lw = lw + ' ' + first

    first = re.match('\S*', phrase_list[k]).group(0)
    if first in PREPOSITIONS:
        bw = bw + ' ' + first
        
    return lw, bw, rw


def get_types(id1, id2):
    return type_from_id(id1), type_from_id(id2)


def outwrite(cl, j, k, id1, id2, phrase_list, sl, outfile):
    lw, bw, rw = get_strings(j, k + j + 1, phrase_list)
    t1, t2 = get_types(id1, id2)
    try:
        outfile.write('\t'.join([str(cl), lw, bw, rw, t1, t2, NAMES_DIC[id1] + '|' + NAMES_DIC[id2] + '|' + sl + "--" + phrase_list[j] + "--" + phrase_list[k+j+1]])
                  + '\n')
    except:
        outfile.write('\t'.join([str(cl), lw, bw, rw, t1, t2, str(id1)  + '|' +  str(id2) + '|' + sl + "--" + phrase_list[j] + "--" + phrase_list[k+j+1]])
                  + '\n')


def process_lists(phrase_list, id_list, outfile, am, ca, fs, sl):
    for j, ids1 in enumerate(id_list):
        for id1 in ids1:
            for k, ids2 in enumerate(id_list[j + 1:]):
                for id2 in ids2:
                    if (id1, id2) in am:
                        outwrite(0, j, k, id1, id2, phrase_list, sl, outfile)
                    if (id1, id2) in ca:
                        outwrite(1, j, k, id1, id2, phrase_list, sl, outfile)
                    if (id1, id2) in fs:
                        outwrite(2, j, k, id1, id2, phrase_list, sl, outfile)
                    if (id2, id1) in am:
                        outwrite(3, j, k, id1, id2, phrase_list, sl, outfile)
                    if (id2, id1) in ca:
                        outwrite(4, j, k, id1, id2, phrase_list, sl, outfile)
                    if (id2, id1) in fs:
                        outwrite(5, j, k, id1, id2, phrase_list, sl, outfile)
                    if (id1, id2) in iss:
                      outwrite(6, j, k, id1, id2, phrase_list, sl, outfile)
                    if (id2, id1) in iss:
                      outwrite(7, j, k, id1, id2, phrase_list, sl, outfile)


print 'Reading Associated Morphology'
am = read_relation(AM_FILE)
print 'Reading Causative Agents'
ca = read_relation(CA_FILE)
print 'Reading Finding Sites'
fs = read_relation(FS_FILE)
print 'Reading Is A'
iss = read_relation(IS_FILE)

NAMES_DIC = snomed2names()

print 'Opening outfile'
o = open(TRAIPATH + 'positive.types.explicit.train', 'w')


for infile in glob(ANNOPATH + '*.annotated'):
    f = open(infile, 'r')
    reader = csv.reader(f, delimiter = '|', quoting=csv.QUOTE_NONE)
    name = re.search('/([^/]+).annotated', infile).group(1)
    #print 'Processing', name
    
    counter = 1

    s = get_sentence(reader)
    while s:
        sl =  name + str(counter)
        counter += 1
        process_lists(s[0], s[1], o, am, ca, fs, sl)
        s = get_sentence(reader)

o.close()
