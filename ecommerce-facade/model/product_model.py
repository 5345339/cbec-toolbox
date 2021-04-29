class Sku:
    def __init__(self, sku, price, storage, shipping_fee, style_color, style_size, image_url,
                 extra_image_list, weight=300):
        self.sku = sku
        self.price = price
        self.storage = storage
        self.shipping_fee = shipping_fee
        self.style_color = style_color
        self.style_size = style_size
        self.image_url = image_url
        self.weight = weight
        self.extra_image_list = extra_image_list


class ProductImage:
    def __init__(self, resource_id, url, goods_sku_list, main_image, sku_image):
        self.resource_id = resource_id
        self.url = url
        self.goods_sku_list = goods_sku_list
        self.main_image = main_image
        self.sku_image = sku_image


class Product:
    def __init__(self, product_id, cat_id, main_image, goods_name, goods_description, parent_sku, sku_list, image_list):
        self.id = product_id
        self.cat_id = cat_id
        self.main_image = main_image
        self.name = goods_name
        self.description = goods_description
        self.parent_sku = parent_sku
        self.sku_list = sku_list
        self.image_list = image_list


class UploadStatus:
    def __init__(self, status, message, product_list):
        self.status = status
        self.message = message
        self.product_list = product_list


class UploadProductDto:
    def __init__(self, cat_id, parent_sku, goods_sku, goods_name, storage, style_color, style_size,
                 goods_description, shop_price, shipping_fee, shipping_weight, main_image,
                 extra_image, extra_image_list):
        self.cat_id = cat_id
        self.parent_sku = parent_sku
        self.goods_sku = goods_sku
        self.goods_name = goods_name
        self.storage = storage
        self.style_color = style_color
        self.style_size = style_size
        self.goods_description = goods_description
        self.shop_price = shop_price
        self.shipping_fee = shipping_fee
        self.main_image = main_image
        self.shipping_weight = shipping_weight
        self.extra_image = extra_image
        self.extra_image_list = extra_image_list

    @staticmethod
    def from_product(product):
        dto_list = []
        for sku in product.sku_list:
            dto = UploadProductDto(cat_id=product.cat_id,
                                   parent_sku=product.parent_sku,
                                   goods_sku=sku.sku,
                                   goods_name=product.name,
                                   goods_description=product.description,
                                   storage=999,
                                   style_color=sku.style_color,
                                   style_size=sku.style_size,
                                   shop_price=sku.price,
                                   shipping_fee=sku.shipping_fee,
                                   shipping_weight=sku.weight,
                                   main_image=product.main_image,
                                   extra_image=sku.image_url,
                                   extra_image_list=[img.url for img in product.image_list
                                                     if not img.main_image and not img.sku_image])
            dto_list.append(dto)
        return dto_list
