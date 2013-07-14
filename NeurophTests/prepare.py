import csv as csv

input_file = csv.reader(open('train.csv', 'r'))
input_file.next()

data = []
for row in input_file:
	data.append(row)

different_labels = []
for row in data:
	if different_labels.count(row[0]) == 0:
		different_labels.append(row[0])

len_diff_labels = len(different_labels)

output_file = open('train.txt', 'w')
for row in data:
	for i in range(1, len(row)):
		output_file.write(row[i])
		output_file.write(',')
	for i in range(len_diff_labels - 1):
		output_file.write('1' if different_labels[i] == row[0] else '0')
		output_file.write(',')
	output_file.write('1' if different_labels[len_diff_labels - 1] == row[0] else '0')
	output_file.write('\n')
output_file.close()