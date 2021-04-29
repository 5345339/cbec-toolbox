# -*- coding: utf-8 -*-

# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: https://docs.scrapy.org/en/latest/topics/item-pipeline.html


from openpyxl import Workbook
class ReptilePipeline(object):
    def __init__(self):
        self.wb = Workbook()
        self.ws = self.wb.active
        self.ws.append(['标题', '公司', '价格', '30天销量(元)', 'method', 'rebuy', '地址', '链接'])

    def process_item(self, item, spider):
        line = [item['title'], item['company'], item['price'], item['sell'], item['method'], item['rebuy'], item['address'], item['detail_url']]
        self.ws.append(line)
        self.wb.save('1688.xlsx')

    def close_spider(self, spider):
        self.wb.close()