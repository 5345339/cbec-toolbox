#!/usr/bin/python

import logging

import os
import requests
from bs4 import BeautifulSoup

from common import exception
from model import order_model

_BASE_URL = "https://merchant.vova.com.hk"

'''
VOVA商家
'''


def build_url(uri):
    return _BASE_URL + uri


def check_response(resp):
    if not resp.ok:
        raise exception.BizException("Http response error".format(resp.status))
    result = resp.json()
    if result["code"] != "SUCCESS":
        raise exception.BizException(result["msg"])


def login(user, password):
    uri = "/index.php?q=admin/main/index/login"

    logging.info("Login with user {}".format(user))

    data = {
        'acct': user,
        'pswd': password,
        'H_sbmt': "yes",
        'verify_code': "",
        'equipment': ""
    }

    resp = requests.post(build_url(uri), data=data, verify=False, timeout=20)
    check_response(resp)
    cookies = requests.utils.dict_from_cookiejar(resp.cookies)
    cookie = ""
    for name, value in cookies.items():
        cookie += '{0}={1};'.format(name, value)
    return cookie


def get_unhandled_order(cookie):
    uri = "/index.php?q=admin/main/unhandledOrder/index&perPage=100"
    response = requests.get(build_url(uri), headers={"cookie": cookie})
    soup = BeautifulSoup(response.text)
    order_list = []
    '''
        def __init__(self, id, type, confirm_time, sn, delivery_count_down, order_cancel_count_down,
                 order_collection_count_down, num, price, total_price, pay_status):
    '''
    order_list_tag = soup.find_all(lambda tag: tag.has_attr('data-order-goods-sn'))
    for order_tag in order_list_tag:
        td_tags = order_tag.find_all("td")
        styles_list = [tag for tag in td_tags[9].contents if isinstance(tag, str)]
        styles_str = "<br>".join(styles_list)
        order = order_model.Order(td_tags[6].a.text,
                                  td_tags[0].a.text,
                                  td_tags[1].text,
                                  td_tags[2].a.text,
                                  td_tags[3].attrs["data-time"],
                                  td_tags[4].attrs["data-time"],
                                  td_tags[5].attrs["data-time"],
                                  td_tags[11].text,
                                  str(td_tags[12].string).split(" ")[1],
                                  td_tags[13].text,
                                  td_tags[14].a.text,
                                  td_tags[8].a.img.attrs["src"],
                                  td_tags[8].a.attrs["href"],
                                  td_tags[8].a.div.span.text,
                                  styles_str)
        order_list.append(order)

    return order_list


def main():
    user = os.getenv("USER")
    password = os.getenv("PASSWORD")
    cookie = login(user, password)
    aa = get_unhandled_order(cookie)
    print(aa)


if __name__ == '__main__':
    main()
