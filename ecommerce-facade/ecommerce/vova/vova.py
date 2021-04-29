import requests

from common import exception
from model import goods_model
from util import json_util, dict_util
from bs4 import BeautifulSoup

_BASE_URL = "https://www.vova.com"

'''
VOVA买家
'''


def _build_vova_url(uri):
    if uri.startswith("/"):
        return _BASE_URL + uri
    return _BASE_URL + "/" + uri


_CATEGORY_BAG_WATCHES = goods_model.Category("bags-watches-accessories", _build_vova_url("Bags-Watches-Accessories-r9876"),
                                 "包、手表、配件")
_CATEGORY_WOMEN_CLOTHING = goods_model.Category("women-s-clothing", _build_vova_url("Women-S-Clothing-r9560"), "女士衣服")
_CATEGORY_MOBILE_PHONES = goods_model.Category("mobile-phones-accessories", _build_vova_url("Mobile-Phones-Accessories-r10045"),
                                   "移动电话、配件")
_CATEGORY_MEN_CLOTHING = goods_model.Category("men-s-clothing", _build_vova_url("Men-S-Clothing-r9881"), "男士衣服")
_CATEGORY_HOME_GARDEN = goods_model.Category("home-garden", _build_vova_url("Home-Garden-r9873"), "家庭")
_CATEGORY_ELECTRONICS = goods_model.Category("electronics", _build_vova_url("Electronics-r9874"), "电子产品")
_COOKIES_ = {}

headers = {
    "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36",
    "Accept": "*/*",
    "Cache-Control": "no-cache",
}


def get_category_by_name(name):
    all_category = get_all_category()
    for i in all_category:
        if str(i.name).strip() == str(name).strip():
            return i
    return None


def get_all_category():
    return [
        _CATEGORY_BAG_WATCHES
    ]


def get_merchant_url_of_goods(goods_url):
    response = requests.get(goods_url)
    _check_response(response)
    soup = BeautifulSoup(response.text)
    merchant_tag = soup.find(class_='prod-info-merchant')
    if not merchant_tag:
        return None
    u = merchant_tag.a["href"]
    return _build_vova_url(u)


def get_category_goods(category, sort="recommended", cursor=None, page_size=20):
    assert isinstance(category, goods_model.Category)

    goods_list = []

    url = category.url + "/" + sort + "?limit=" + str(page_size) + "&is_ajax=1"
    if cursor:
        url += "&after=" + cursor

    res = requests.get(url, headers=headers)
    _check_response(res)

    res_dict = json_util.json2dict(res.text)
    next_page_cursor = res_dict["data"]["pagination"]["cursors"]["after"]
    product_list = res_dict["data"]["productList"]

    for product_dict in product_list:
        product_obj = dict_util.dict2obj(product_dict)
        url = _build_vova_url(product_obj.url)
        merchant_url = get_merchant_url_of_goods(url)
        goods_list.append(goods_model.GoodsInfo(
            product_obj.virtual_goods_id,
            product_obj.name,
            category.name,
            url,
            product_obj.shop_price_exchange,
            "",
            merchant_url,
            platform="vova"
        ))

    idx = 0
    product_list_ext = dict_util.dict2obj(res_dict["data"]["arEcommerce"]["listProducts"][0]["product_list"])
    for product_ext_dict in product_list_ext:
        product_ext_obj = dict_util.dict2obj(product_ext_dict)
        goods_list[idx].image_url = "https://" + product_ext_obj.picture
        idx = idx + 1

    return goods_model.ScrollResult(cursor, next_page_cursor, goods_list)


def get_product_info_by_url(product_url):
    info = {}
    return info


def get_merchant_product_top_n(merchant_id, top_n=15):
    uri = _build_vova_url("merchant-" + merchant_id)

    response = requests.get(uri)
    _check_response(response)

    product_list = []

    soup = BeautifulSoup(response.text)
    product_list_tag = soup.find_all(_class="cat-grid-link-wrap")
    product_link_list = [p.div.div.a["href"] for p in product_list_tag]
    if not product_link_list:
        return product_list

    for product_link in product_link_list:
        info = get_product_info_by_url(_build_vova_url(product_link))
        if not info:
            continue



def _check_response(response):
    if response.status_code != 200:
        raise exception.BizException(response.text)


def main():
    goods_list = get_category_goods(_CATEGORY_BAG_WATCHES)
    print(json_util.obj2json(goods_list))


if __name__ == '__main__':
    main()
