import datetime
import logging

from flask import Blueprint, request, Response

from common import exception
from ecommerce.ali1688 import ali1688
from ecommerce.vova import vova
from ecommerce.vova import vova_merchant_rest
from util import json_util

goods = Blueprint('goods', __name__)


def response_json_data(response):
    return Response(response, mimetype='application/json')


@goods.route('/list_all_category/<platform>', methods=['GET'])
def list_all_category(platform):
    category_list = vova.get_all_category()
    response = json_util.obj2json(category_list)
    return response_json_data(response)


@goods.route('/list_category_goods/<platform>', methods=['GET'])
def list_category_goods(platform):
    category_name = request.args.get("category")
    if not category_name:
        raise exception.BizException("类目不能为空")

    sort = request.args.get("sort")
    if not sort:
        sort = "recommended"

    cursor = request.args.get("cursor")

    category_info = vova.get_category_by_name(category_name)
    if not category_info:
        raise exception.BizException("无效的类目")

    start_time = datetime.datetime.now()
    response = json_util.obj2json(vova.get_category_goods(category_info, sort, cursor))
    end_time = datetime.datetime.now()

    logging.info("list_category_goods cost {}(s)".format((end_time - start_time).seconds))

    return response_json_data(response)


@goods.route('/search_goods_by_image', methods=['GET'])
def search_goods_by_image():
    image_url = request.args.get("image_url")
    max_price = request.args.get("max_price")
    if not image_url or not max_price:
        raise ValueError("无效参数")

    num = request.args.get("num")
    if not num:
        num = 5

    start_time = datetime.datetime.now()
    response = json_util.obj2json(ali1688.search_goods_by_image(image_url, float(max_price), int(num)))
    end_time = datetime.datetime.now()

    logging.info("search_goods_by_image cost {}(s)".format((end_time - start_time).seconds))

    return response_json_data(response)


@goods.route('/sync_product/<platform>', methods=['GET'])
def sync_product(platform):
    api_token = request.args.get("apiToken")
    start_time = request.args.get("startTime")
    end_time = request.args.get("endTime")
    if not api_token or not start_time or not end_time:
        raise ValueError("Invalid params")

    response = json_util.obj2json(vova_merchant_rest.get_product_list(api_token, start_time, end_time))
    return response_json_data(response)


@goods.route('/upload_product/<platform>', methods=['POST'])
def upload_product(platform):
    api_token = request.args.get("apiToken")


@goods.route('/upload_status/<platform>', methods=['GET'])
def get_upload_status(platform):
    api_token = request.args.get("apiToken")
    upload_id = request.args.get("uploadId")
    if not api_token or not upload_id:
        raise ValueError("Invalid params")

    vova_merchant_rest.get_upload_status_by_batch_id(api_token, upload_id)


if __name__ == '__main__':
    category = vova.get_category_by_name("bags-watches-accessories")
    scroll_result = vova.get_category_goods(category, sort="most-popular")
    aa = vova.get_category_goods(category, sort="most-popular", cursor=scroll_result.next_cursor)
    bb = vova.get_category_goods(category, cursor=aa.next_cursor)
    if str(aa.next_cursor) == str(bb.next_cursor):
        print("----ok----")
    #
    # goods_list = scroll_result.results
    # for goods in goods_list:
    #     print("----------------------------------------------")
    #     print(json_util.obj2json(goods))
    #     print("##############################################")
    #     sum = 0
    #     ali_goods_list = ali1688.search_goods_by_image(goods.image_url, goods.price * 3)
    #     for g in ali_goods_list:
    #         print(json_util.obj2json(g))
    #         sum = sum + float(g.price)
    #     if len(ali_goods_list) > 0:
    #         print("$$$$$$$$$$$$$$- {} -$$$$$$$$$$$$$$$$$$$$".format(sum / len(ali_goods_list)))
