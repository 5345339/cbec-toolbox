from scrapy import cmdline


def main():
    cmdline.execute('scrapy crawl 1688'.split())


if __name__ == "__main__":
    main()
