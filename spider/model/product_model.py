class Sku:
    def __init__(self, goods_sku, shop_price, storage, shipping_fee, style_info):
        self.goods_sku = goods_sku
        self.shop_price = shop_price
        self.storage = storage
        self.shipping_fee = shipping_fee
        self.style_info = style_info


class ProductImage:
    def __init__(self, img_id, img_url, sequence, goods_sku_list):
        self.img_id = img_id
        self.img_url = img_url
        self.sequence = sequence
        self.goods_sku_list = goods_sku_list


class Product:
    def __init__(self, product_id, cat_id, main_image, goods_name, goods_description, parent_sku, sku_list, image_list):
        self.product_id = product_id
        self.cat_id = cat_id
        self.main_image = main_image
        self.goods_name = goods_name
        self.goods_description = goods_description
        self.parent_sku = parent_sku
        self.sku_list = sku_list
        self.image_list = image_list


class UploadProductDto:
    def __init__(self, cat_id, parent_sku, goods_sku,
                 goods_name, goods_name_fr="", goods_name_de="", goods_name_es="", goods_name_it="",
                 storage=999,
                 goods_description="", goods_description_fr="", goods_description_de="", goods_description_es="",
                 goods_description_it="",
                 tags="", goods_brand="",
                 market_price=0, shop_price=0, shipping_fee=0, main_image=""):
        self.cat_id = cat_id
        self.parent_sku = parent_sku
        self.goods_sku = goods_sku
        self.goods_name = goods_name
        self.goods_name_fr = goods_name_fr
        self.goods_name_de = goods_name_de
        self.goods_name_es = goods_name_es
        self.goods_name_it = goods_name_it
        self.storage = storage
        self.goods_description = goods_description
        self.goods_description_fr = goods_description_fr
        self.goods_description_de = goods_description_de
        self.goods_description_es = goods_description_es
        self.goods_description_it = goods_description_it
        self.tags = tags
        self.goods_brand = goods_brand
        self.market_price = market_price
        self.shop_price = shop_price
        self.shipping_fee = shipping_fee
        self.main_image = main_image
