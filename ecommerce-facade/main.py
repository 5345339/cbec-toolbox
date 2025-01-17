import logging

import flask

from controller.goods_controller import *
from controller.order_controller import *
from controller.translate_controller import *
from common.exception_advice import *

app = flask.Flask(__name__)

logging.basicConfig(level=logging.INFO,
                    format='%(asctime)s - %(filename)s[line:%(lineno)d] - %(levelname)s: %(message)s')


def main():
    app.register_blueprint(exception_advice, url_prefix="/")
    app.register_blueprint(order, url_prefix='/order')
    app.register_blueprint(goods, url_prefix='/goods')
    app.register_blueprint(translate, url_prefix='/translate')
    app.run(host="0.0.0.0", port=33023, debug=False, threaded=True)


if __name__ == '__main__':
    main()
