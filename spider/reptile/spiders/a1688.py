# -*- coding: utf-8 -*-
import scrapy
from bs4 import BeautifulSoup
from scrapy.http import HtmlResponse
import json
import logging

from Reptile.items import ReptileItem
from urllib.parse import quote
from scrapy_splash import SplashRequest


KEYWORD = "春夏女装"
PAGE = 1

class A1688Spider(scrapy.Spider):
    def __init__(self):
        self.key = quote(KEYWORD.encode("gb2312"))
        self.item = ReptileItem()
        self.item['title'] = ''
        self.item['company'] = ''
        self.item['price'] = ''
        self.item['sell'] = ''
        self.item['rebuy'] = ''
        self.item['method']  = ''
        self.item['address'] = ''
        self.item['detail_url'] = ''

    name = '1688'

    # allowed_domains = ['s.1688.com']
    # start_urls = ['http://s.1688.com/']
    def start_requests(self):
        
        url = "https://s.1688.com/selloffer/offer_search.htm?keywords={0}&n=y&netType=1%2C11&encode=utf-8&spm=a260k.dacugeneral.search.0".format(self.key)
        yield scrapy.Request(url=url, callback=self.parse)

    def parse(self, response):
        #maxpage = response.xpath('//*/div/@data-total-page').extract_first()
        maxpage = 1
        max =  PAGE if (PAGE>0 and PAGE<int(maxpage)) else int(maxpage)+1
        for page in range(0,  max):
            url = 'https://s.1688.com/selloffer/offer_search.htm?keywords=%s&n=y&netType=16&beginPage=%s#sm-filtbar' % (self.key, page + 1)
            yield scrapy.Request(url=url, callback=self.parse_datas)

    def parse_datas(self, response):
        assert isinstance(response, HtmlResponse)

        result_data_str = "window.data.offerresultData = successDataCheck("
        begin_idx = response.text.find(result_data_str)
        if begin_idx <= 0:
            return None
        begin_idx = begin_idx + len(result_data_str)
        end_idx = response.text.find("\n", begin_idx)
        if end_idx <= 0:
            return None
        end_idx = end_idx - 2
        offer_result_data_json = response.text[begin_idx:end_idx]

        offer_result_data_dict = json.loads(offer_result_data_json)
        if offer_result_data_dict["code"] != 200:
            logging.error("Invalid offer data")
            return None

        offer_list = offer_result_data_dict["data"]["offerList"]
        for offer in offer_list:
            try:
                information = offer["information"]
                self.item["title"] = information["brief"]
                self.item["company"] = offer["company"]["name"]
                self.item["detail_url"] = information["detailUrl"]
                yield scrapy.Request(url=information["detailUrl"], callback=self.parse_goods)
            except Exception as e:
                logging.error("error when parse offer, error {}".format(e))
                continue

    def parse_goods(self, response):
        assert isinstance(response, HtmlResponse)

        for tag in response.css('.table-sku').extract():
            soup = BeautifulSoup(tag, 'lxml')
            self.item['title'] = soup.select(".sw-dpl-offer-photo img")[0].attrs['alt']


        # for offer in offer_list:
        #     try:
        #         information = offer["information"]
        #         self.item["title"] = information["brief"]
        #         self.item["company"] = offer["company"]["name"]
        #         self.item["detail_url"] = information["detailUrl"]
        #         yield self.item
        #     except Exception as e:
        #         logging.error("error when parse offer, error {}".format(e))
        #         yield self.item
        #         continue

        # for tag in response.css('.space-offer-card-box').extract():
        #     try:
        #         # 从response中利用css选择器提取出来的标签是文本形式，需要利用 BeautifulSoup 转换成BeautifulSoup.Tag 对象进行进一步提取。
        #         soup = BeautifulSoup(tag, 'lxml')
        #
        #         self.item['title'] = soup.select(".sw-dpl-offer-photo img")[0].attrs['alt']
        #         self.item['company'] = soup.select(".sw-dpl-offer-companyName")[0].attrs['title']
        #         self.item['price'] = soup.select(".sw-dpl-offer-priceNum")[0].attrs['title']
        #         # print("------------price---", self.item['price'])
        #         if self.item['price'] and self.item['price'] != '':
        #             self.item['price'] = self.item['price'][1:]
        #         self.item['sell'] = soup.select(".sm-offer-tradeBt")[0].attrs['title']
        #         # print("------------sell---", self.item['sell'])
        #         if self.item['sell'] and self.item['sell'] != "":
        #             self.item['sell'] = self.item['sell'][5:-1]
        #         self.item['rebuy'] =soup.select(".sm-widget-offershopwindowshoprepurchaserate span")[2].string
        #         self.item['method']  = soup.select(".sm-widget-offershopwindowshoprepurchaserate i")[0].string
        #         #对于不一定能获取的数据，需要判断数据存在与否。
        #         if soup.select(".sm-offer-location")[0].attrs['title']:
        #             address= soup.select(".sm-offer-location")[0].attrs['title']
        #         else:
        #             address = ""
        #         self.item['address'] = address
        #
        #         if soup.select(".sm-offer-subicon a"):
        #             subicon = []
        #             for i in soup.select(".sm-offer-subicon a"):
        #                 subicon.append(i.attrs['title'] + ',')
        #             subicon = "".join(subicon)
        #             self.item['subicon']=subicon
        #         else:
        #             self.item['subicon'] = ' '
        #         #返回这个数据模型，交给 ITEM_PIPELINES 处理
        #         print("---------------sucsess-item:", dict(self.item))
        #         yield self.item
        #     except Exception as error:
        #         print("--------------error--item:", dict(self.item))
        #         yield self.item
        #         print("出错了:", error)
        #         continue