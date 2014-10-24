import csv
import re
from glob import glob
from loadDictionaries import snomed2names

CURRENTPATH = 'D:/learningDL-master/'

ROLEPATH = CURRENTPATH
AM_FILE = ROLEPATH + 'Associated morphology (attribute).csv'
CA_FILE = ROLEPATH + 'Causative agent (attribute).csv'
FS_FILE = ROLEPATH + 'Finding site (attribute).csv'
TRAIPATH = CURRENTPATH
ANNOPATH = CURRENTPATH + 'WikipediaAnnotated/'

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
        lw = '*'
    
    if k+1 < len(phrase_list):
        rw = phrase_list[k + 1]
    else:
        rw = '*'

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


def outwrite(cl, j, k, id1, id2, phrase_list, sl, no, outfile):
    lw, bw, rw = get_strings(j, k + j + 1, phrase_list)
    if id1[:1] == '-' and id2[:1] == '-':
        id11 = int(id1[1:])
        id22 = int(id2[1:])
        t1, t2 = get_types(id11, id22)
        try:
            outfile.write('\t'.join([cl, lw, bw, rw, t1, t2, "~" + NAMES_DIC[id11] + '|' + "~" + NAMES_DIC[id22] + '|' + sl + "--" + phrase_list[j] + "--" + phrase_list[k+j+1] + '--' + no + '|' + lw + '|' + bw + '|' + rw + '|' + t1 + '|' + t2])
                  + '\n')
        except:
            outfile.write('')
    elif id1[:1] == '-':
        id11 = int(id1[1:])
        id22 = int(id2)
        t1, t2 = get_types(id11, id22)
        try:
            outfile.write('\t'.join([cl, lw, bw, rw, t1, t2, "~" + NAMES_DIC[id11] + '|' + NAMES_DIC[id22] + '|' + sl + "--" + phrase_list[j] + "--" + phrase_list[k+j+1] + '--' + no + '|' + lw + '|' + bw + '|' + rw + '|' + t1 + '|' + t2])
                  + '\n')
        except:
            outfile.write('')
    elif id2[:1] == '-':
        id11 = int(id1)
        id22 = int(id2[1:])
        t1, t2 = get_types(id11, id22)
        try:
            outfile.write('\t'.join([cl, lw, bw, rw, t1, t2, NAMES_DIC[id11] + '|' + "~" + NAMES_DIC[id22] + '|' + sl + "--" + phrase_list[j] + "--" + phrase_list[k+j+1] + '--' + no + '|' + lw + '|' + bw + '|' + rw + '|' + t1 + '|' + t2])
                  + '\n')
        except:
            outfile.write('')
    else:
        id11 = int(id1)
        id22 = int(id2)
        t1, t2 = get_types(id11, id22)
        try:
            outfile.write('\t'.join([cl, lw, bw, rw, t1, t2, NAMES_DIC[id11] + '|' + NAMES_DIC[id22] + '|' + sl + "--" + phrase_list[j] + "--" + phrase_list[k+j+1] + '--' + no + '|' + lw + '|' + bw + '|' + rw + '|' + t1 + '|' + t2])
                  + '\n')
        except:
            outfile.write('')
        


def process_lists(phrase_list, id_list, outfile, sl, no):
    for j, ids1 in enumerate(id_list):
        for id1 in ids1:
            for k, ids2 in enumerate(id_list[j + 1:]):
                for id2 in ids2:
                    outwrite("", j, k, str(id1), str(id2), phrase_list, sl, no, outfile)
 



NAMES_DIC = snomed2names()

print 'Opening outfile'
o = open(TRAIPATH + 'positive.types.explicit.test', 'w')


for infile in glob(ANNOPATH + '@New.annotated'):
    f = open(infile, 'r')
    reader = csv.reader(f, delimiter = '|', quoting=csv.QUOTE_NONE)
    name = re.search('/([^/]+).annotated', infile).group(1)
    print 'Processing', name
    
    counter = 1

    s = get_sentence(reader)
    while s:
        no = str(counter) 
        sl =  name + no
        process_lists(s[0], s[1], o, sl, no)
        counter += 1
        s = get_sentence(reader)

o.close()
