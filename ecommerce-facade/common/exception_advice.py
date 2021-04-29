
from flask import Blueprint, jsonify
from common import exception
import logging

exception_advice = Blueprint('exception', __name__)


@exception_advice.app_errorhandler(exception.BizException)
def handle_biz_exception(error):
    logging.error("Business exception: {}".format(error))
    response = jsonify(error.to_dict())
    response.status_code = error.status
    return response