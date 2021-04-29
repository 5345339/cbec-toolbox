from flask import request, Blueprint, Response

from util import json_util


translate = Blueprint('translate', __name__)