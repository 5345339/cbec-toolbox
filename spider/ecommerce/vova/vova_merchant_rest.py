#!/usr/bin/python

import logging
import os
from util.json_util import obj2json, json2dict, json2obj
from model import product_model
from common import exception

import requests
from bs4 import BeautifulSoup

from common import exception
from model import order_model

_BASE_URL = "https://merchant.vova.com.hk/api/v1"

'''
VOVA商家
'''


def build_url(uri):
    return _BASE_URL + uri


def check_response(resp):
    if not resp.ok:
        raise exception.BizException("Http response error".format(resp.status))


def json_header():
    headers = {"Content-Type": "application/json"}
    return headers


def get_product_image(token, product_id):
    uri = "/product/getProductImgInfo"

    response = requests.get(build_url(uri) + "?token=" + token + "&product_id=" + product_id)
    check_response(response)

    img_list_obj = []
    response_dict = json2dict(response.text)
    product_img_list = response_dict["product_img_info"]
    for img in product_img_list:
        img_id = img["img_id"]
        img_url = img["img_url"]
        sequence = img["sequence"]
        goods_sku_list = img["goods_sku_list"]
        img_list_obj.append(product_model.ProductImage(img_id, img_url, sequence, goods_sku_list))
    return img_list_obj


def get_product_list(token, start_time, end_time):
    uri = "/product/productList"

    page = 1
    per_page = 100

    product_list_obj = []

    while True:
        params = {
            'token': token,
            'conditions': {
                'add_time': {
                    'start': start_time,
                    'end': end_time
                },
                'page_arr': {
                    'perPage': per_page,
                    'page': page
                }
            }
        }

        response = requests.post(build_url(uri), headers=json_header(), data=obj2json(params))
        check_response(response)
        response_dict = json2dict(response.text)

        page_arr = response_dict["page_arr"]
        total_page = int(page_arr["totalPage"])

        page = page+1
        if page > total_page + 1:
            break

        product_list = response_dict["product_list"]
        if not product_list:
            continue

        for product in product_list:
            is_on_sale = bool(product["is_on_sale"])
            if not is_on_sale:
                continue

            product_id = product["product_id"]
            cat_id = product["cat_id"]
            main_image = product["main_image"]
            goods_name = product["goods_name"]
            goods_description = product["goods_description"]
            parent_sku = product["parent_sku"]
            sku_list = product["sku_list"]
            if not sku_list:
                continue
            sku_list_obj = []
            for sku in sku_list:
                goods_sku = sku["goods_sku"]
                shop_price = sku["shop_price"]
                storage = sku["storage"]
                shipping_fee = sku["shipping_fee"]
                style_info = sku["style_info"]
                sku_list_obj.append(product_model.Sku(goods_sku, shop_price, storage, shipping_fee, style_info))

            product_image_list = get_product_image(token, product_id)
            product_list_obj.append(product_model.Product(product_id, cat_id, main_image, goods_name, goods_description,
                                                          parent_sku, sku_list_obj, product_image_list))

    return product_list_obj


def add_product_sku(token, parent_sku, goods_sku, storage, market_price, shop_price, shipping_fee, sku_image):
    uri = "/product/addProductSku"

    params = {
        "token": token,
        "items": [
            {
                "parent_sku": parent_sku,
                "goods_sku": goods_sku,
                "storage": storage,
                "market_price": market_price,
                "shop_price": shop_price,
                "shipping_fee": shipping_fee,
                "sku_image": sku_image,
            }
        ]
    }

    response = requests.post(build_url(uri), headers=json_header(), data = obj2json(params))
    check_response(response)
    response_dict = json2dict(response.text)

    if response_dict["data"]["code"] != 0:
        raise exception.BizException("Failed to add product sku")


def upload_product(token, product_list_dto):
    uri = "/product/uploadGoods"
    response = requests.post(build_url(uri), headers=json_header(), data=obj2json(product_list_dto))
    check_response(response)
    response_dict = json2dict(response.text)
    if response_dict["data"]["code"] != 0:
        raise exception.BizException("Failed to add upload product")


def main():
    token = os.getenv("TOKEN")
    if not token:
        raise ValueError("No token")

    start_time = '2020-12-04 00:00:00'
    end_time = '2020-12-04 16:00:00'
    aa = get_product_list(token, start_time, end_time)
    print(aa)


if __name__ == '__main__':
    main()
