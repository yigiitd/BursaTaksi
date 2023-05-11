import json

latitudes = open('latitudes.txt', 'r', encoding='utf-8').readlines()
longitudes = open('longitudes.txt', 'r', encoding='utf-8').readlines()
names = open('names.txt', 'r', encoding='utf-8').readlines()
phone_numbers = open('phone_numbers.txt', 'r', encoding='utf-8').readlines()

liste = []

for i in range(141):
    dictionary: dict[str, str] = {
        "id": 1000 + i,
        "name": names[i].strip(),
        "phone_number": phone_numbers[i].strip(),
        "latitude": latitudes[i].strip(),
        "longitude": longitudes[i].strip(),
    }

    liste.append(dictionary)


json_data = json.dumps(liste, indent=None, ensure_ascii=False)
file = open("station_data.json", 'x', encoding='utf-8')
file.write(json_data)
file.close()