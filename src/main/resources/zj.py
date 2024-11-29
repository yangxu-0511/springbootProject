import requests
import re
import json
result = {}
url_page = "https://webapi.sporttery.cn/gateway/lottery/getHistoryPageListV1.qry?gameNo=85&provinceId=0&pageSize=30&isVerify=1&pageNo=1"

response_page = requests.get(url_page)
json_data_page = response_page.json()
data_page = json_data_page['value']
pages = data_page['pages']
url = "https://webapi.sporttery.cn/gateway/lottery/getHistoryPageListV1.qry?gameNo=85&provinceId=0&pageSize=30&isVerify=1&pageNo=1"  # 使用占位符
for page_no in range(1, 2):  # 只处理第一页
    response = requests.get(url.format(page_no))  # 正确格式化URL
    if response.status_code == 200:  # 检查请求是否成功
        try:
            json_data = response.json()  # 尝试解析 JSON
            data = json_data['value']
            for record in data['list'][:5]:  # 只获取前五条记录
                phone = record['lotteryDrawResult']
                modified_phone = re.sub(r'\s', '|', phone)
                result[record['lotteryDrawTime']] = modified_phone
        except json.JSONDecodeError:
            print(f"Failed to decode JSON for page {page_no}. Response text: {response.text}")
# 移动文件写入到循环外部
filename = "dlt_bak.json"
with open(filename, "w") as json_file:
    json.dump(result, json_file)
print(f"Data has been written to {filename}")

result2 = {}
url2 = "http://www.cwl.gov.cn/cwl_admin/front/cwlkj/search/kjxx/findDrawNotice?name=ssq&issueCount=&issueStart=&issueEnd=&dayStart=&dayEnd=&week=&systemType=PC&pageNo=1&pageSize=96000000" 
response2 = requests.get(url2)
json_data2 = response2.json()
data_list2 = json_data2['result']
for record in data_list2:
    phone1 = record['red']
    phone2 = record['blue']
    modified_phone = re.sub(',', '|', phone1)+'|'+phone2
    result2[record['date'][0:10]] = modified_phone

# 指定要写入的文件名
filename2 = "ssq.json"
# 使用with语句打开文件并将数据写入
with open(filename2, "w") as json_file2:
    json.dump(result2, json_file2)
print(f"Data has been written to {filename2}")