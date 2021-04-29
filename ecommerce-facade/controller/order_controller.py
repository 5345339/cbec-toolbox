from flask import request, Blueprint, Response

from common import exception
from util import json_util
from ecommerce.vova import vova_merchant_rest

order = Blueprint('order', __name__)


@order.route('/list_unhandled_order/<platform>', methods=['GET'])
def list_unhandled_order(platform):
    api_token = request.args.get("apiToken")
    if not api_token:
        raise ValueError("Invalid params")

    return response_json_data(vova_merchant_rest.list_unhandled_order(api_token))


def response_json_data(response):
    assert not isinstance(response, str)
    return Response(json_util.obj2json(response), mimetype='application/json')