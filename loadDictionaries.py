#! /usr/bin/env python
import csv
from os import path

this_dir, this_file = path.split(__file__)
DICPATH = path.join(this_dir, 'Metathesaurus')


def umls2snomed():
    sno_dic = {}

    with open(path.join(DICPATH, 'c2s.csv'), 'rb') as f:
        reader = csv.reader(f, delimiter = '|')

        for line in reader:
            cuid = line[0]
            try:
                sid = int(line[1])
            except ValueError:
                print 'Cannot process ', cuid

            try:
                sno_dic[cuid].append(sid)
            except KeyError:
                sno_dic[cuid] = [sid]

    return sno_dic

def snomed2names():
    sno_dic = {}

    with open(path.join(DICPATH, 's2n.csv'), 'rb') as f:
        reader = csv.reader(f, delimiter = '|')

        for line in reader:
            name = line[1]
            try:
                sid = int(line[0])
            except ValueError:
                print 'Cannot process ', line[0]

            sno_dic[sid] = name

    return sno_dic

def umls2names():
    dic = {}
    u2s = umls2snomed()
    s2n = snomed2names()

    for cuid in u2s:
        dic[cuid] = []
        for sid in u2s[cuid]:
            try:
                dic[cuid].append(s2n[sid])
            except KeyError:
                continue

    return dic
