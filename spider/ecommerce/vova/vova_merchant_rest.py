#!/usr/bin/python

import logging
import os

import requests

from common import exception
from model import product_model
from util.json_util import obj2json, json2dict

_BASE_URL = "https://merchant.vova.com.hk/api/v1"
_SUCCESS_CODE = 20000
_PRODUCT_ALREADY_EXIST_ERROR_CODE = 40015
_SKU_IMAGE_MAX_NUM = 20
_EXECUTE_SUCCESS = 'success'

'''
VOVA商家
'''

_DEFAULT_TIMEOUT = 10


def build_url(uri):
    return _BASE_URL + uri


def check_response(resp):
    if not resp.ok:
        raise exception.BizException("Http error, status={}, msg={}".format(resp.status_code, resp.text))


def json_header():
    headers = {"Content-Type": "application/json"}
    return headers


def get_product_image(token, product_id):
    uri = "/product/getProductImgInfo"

    img_list_obj = []
    succeed = False
    retry_times = 5
    while not succeed and retry_times > 0:
        response_text = ''
        try:
            response = requests.get(build_url(uri) + "?token=" + token + "&product_id=" + product_id,
                                    timeout=_DEFAULT_TIMEOUT)
            check_response(response)
            response_text = response.text
        except Exception as err:
            logging.error("Error: {}, remain retry times {}".format(err, retry_times))
            retry_times = retry_times - 1
            continue

        succeed = True
        response_dict = json2dict(response_text)
        product_img_list = response_dict["product_img_info"]
        for img in product_img_list:
            img_id = img["img_id"]
            img_url = img["img_url"]
            goods_sku_list = img["goods_sku_list"]
            is_main_img = True
            is_main_img_str = img['main_img']
            if 'N' == is_main_img_str:
                is_main_img = False
            is_sku_image = True
            if not goods_sku_list:
                is_sku_image = False
            img_list_obj.append(product_model.ProductImage(img_id, img_url, goods_sku_list, is_main_img, is_sku_image))

    return img_list_obj


def get_product_list(token, start_time, end_time):
    page = 1
    total_page = 1
    result_list = []
    while page <= total_page:
        total_page, product_list = get_product_list_by_page(token, start_time, end_time, page)
        if total_page <= 0 or not product_list:
            break
        page = page + 1
        result_list.extend(product_list)
    return result_list


def get_product_list_by_page(token, start_time, end_time, page=1, per_page=100):
    uri = "/product/productList"

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

    response_text = ""
    try:
        response = requests.post(build_url(uri), headers=json_header(), data=obj2json(params),
                                 timeout=_DEFAULT_TIMEOUT)
        check_response(response)
        response_text = response.text
    except Exception as err:
        logging.error("Error: {}".format(err))
        return 0, None

    response_dict = json2dict(response_text)
    page_arr = response_dict["page_arr"]
    total_page = int(page_arr["totalPage"])
    total_amount = int(page_arr["totalAmount"])

    logging.info("Total page {} and total num {} between {}-{}, current page {}"
                 .format(total_page, total_amount, start_time, end_time, page))

    if page > total_page:
        # logging.error("Page {} greater than total {}".format(page, total_page))
        return 0, None

    product_list = response_dict["product_list"]
    if not product_list:
        return 0, None

    logging.info("Found {} product".format(len(product_list)))

    product_list_obj = []
    count = 0
    for product in product_list:
        complete_ratio = round(count / len(product_list) * 100, 2)
        count = count + 1

        is_on_sale = bool(product["is_on_sale"])
        if not is_on_sale:
            continue

        product_id = product["product_id"]
        cat_id = product["cat_id"]
        main_image = product["main_image"]
        goods_name = product["goods_name"]
        goods_description = product["goods_description"]
        parent_sku = product["parent_sku"]

        product_image_list = get_product_image(token, product_id)
        if not product_image_list:
            continue

        sku_list = product["sku_list"]
        if not sku_list:
            continue
        sku_obj_list = []
        for sku in sku_list:
            goods_sku = sku["goods_sku"]
            shop_price = sku["shop_price"]
            storage = sku["storage"]
            shipping_fee = sku["shipping_fee"]
            style_info = json2dict(sku["style_info"])
            style_color = ""
            if "color" in style_info:
                style_color = style_info["color"]
            style_size = ""
            if "size" in style_info:
                style_size = style_info["size"]

            sku_image_list = []
            sku_image_list.extend([s.url for s in product_image_list if goods_sku in s.goods_sku_list])
            extra_image_list = []
            extra_image_list.extend([s.url for s in product_image_list if not s.goods_sku_list])
            # 图片超过最大数量时，去掉部分图片
            if len(extra_image_list) > _SKU_IMAGE_MAX_NUM:
                logging.warning("Image number exceed, num={}, max={}"
                                .format(len(extra_image_list), _SKU_IMAGE_MAX_NUM))
                extra_image_list = extra_image_list[0:_SKU_IMAGE_MAX_NUM]
            sku_obj_list.append(product_model.Sku(goods_sku, shop_price, storage, shipping_fee, style_color,
                                                  style_size, sku_image_list[0], extra_image_list))

        product_list_obj.append(product_model.Product(product_id, cat_id, main_image, goods_name,
                                                      goods_description, parent_sku, sku_obj_list, product_image_list))

        logging.info("[{}%] Process product {} - {}".format(complete_ratio, product_id, goods_name))

    return total_page, product_list_obj


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

    response = requests.post(build_url(uri), headers=json_header(), data=obj2json(params))
    check_response(response)
    response_dict = json2dict(response.text)

    if response_dict["data"]["code"] != _SUCCESS_CODE:
        raise exception.BizException("Failed to add product sku")


def upload_product(token, product_list_dto):
    uri = "/product/uploadGoods"

    params = {
        "token": token,
        "items": product_list_dto,
        "ignore_warning": 0,
        "is_cn": 0,
    }

    data = obj2json(params).encode("utf-8").decode("latin1")
    response = requests.post(build_url(uri), headers=json_header(), data=data)
    check_response(response)
    response_dict = json2dict(response.text)
    response_data = response_dict["data"]
    if response_data["code"] != _SUCCESS_CODE:
        if response_data["code"] == _PRODUCT_ALREADY_EXIST_ERROR_CODE:
            logging.warning("Product already exist, ignore")
        else:
            errs = response_data["message"]
            if "error_list" in response_data:
                errs = errs + "; " + obj2json(response_data["errors_list"])
            raise exception.BizException("Failed to upload product, {}".format(errs))

    return response_data["upload_batch_id"]


def get_upload_status_by_batch_id(token, upload_batch_id):
    uri = "/product/getUploadGoodsStatus"

    params = {
        "token": token,
        "conditions": {
            "upload_batch_id": upload_batch_id
        }
    }

    response = requests.post(build_url(uri), headers=json_header(), data=obj2json(params))
    check_response(response)
    response_dict = json2dict(response.text)

    status = response_dict["code"]
    message = response_dict["message"]
    product_id_list = []

    if 'success' == status:
        status = 'uploaded'
        succeed_product_list = response_dict["data"]
        if succeed_product_list:
            product_id_list.extend([{p["product_id"], p['parent_sku']} for p in succeed_product_list])
    else:
        status = 'prepare'

    return product_model.UploadStatus(status, message, product_id_list)


def enable_product_sale(token, product_id_list):
    uri = "/product/enableSale"

    params = {
        "token": token,
        "goods_list": product_id_list,
    }

    response = requests.post(build_url(uri), headers=json_header(), data=obj2json(params))
    check_response(response)

    response_dict = json2dict(response.text)
    response_data = response_dict["data"]

    code = response_data["code"]
    if code != _SUCCESS_CODE:
        message = response_data["message"]
        errors_list = response_data["errors_list"]
        message = message + "; => " + obj2json(errors_list)
        raise exception.BizException(message)


def delete_product(token, product_id_list):
    uri = "/product/deleteGoods"

    params = {
        "token": token,
        "goods_list": product_id_list
    }

    response = requests.post(build_url(uri), headers=json_header(), data=obj2json(params))
    check_response(response)

    response_dict = json2dict(response.text)
    if response_dict["execute_status"] != _EXECUTE_SUCCESS:
        raise exception.BizException("Delete product failed, {}".format(response_dict["message"]))


def copy_product(src_token, dst_token, start_time, end_time, page=1):
    if not src_token or not dst_token:
        raise ValueError("Invalid token")
    if not start_time or not end_time:
        raise ValueError("Invalid start/end time")

    product_list = get_product_list_by_page(src_token, start_time, end_time, page, 25)
    if not product_list:
        print("No product found")
        return

    dto_list = []
    for product in product_list:
        dto_list.extend(product_model.UploadProductDto.from_product(product))

    logging.info("Found {} product, total sku num {}".format(len(product_list), len(dto_list)))

    max_sku_num_per_time = 300
    if len(dto_list) >= max_sku_num_per_time:
        logging.error("Sku num exceed, max is {}".format(max_sku_num_per_time))
        return

    upload_id = upload_product(dst_token, dto_list)
    print("Upload product succeed, upload id {}".format(upload_id))
    # succeed_product_id_list = get_upload_status_by_batch_id(dst_token, upload_id)


if __name__ == '__main__':
    logging.basicConfig(level=logging.INFO,
                        format='%(asctime)s - %(filename)s[line:%(lineno)d] - %(levelname)s: %(message)s')

    copy_product(os.getenv("SRC_TOKEN"), os.getenv("DST_TOKEN"), '2020-12-13 00:00:00', '2020-12-13 23:59:59', 1)
