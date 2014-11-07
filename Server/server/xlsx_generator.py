import xlsxwriter
import StringIO
import logging

def generateReport(title, order_items, customer_order_map, customers):
    logging.info(str(order_items))
    logging.info(str(customer_order_map))
    output = StringIO.StringIO()

    workbook = xlsxwriter.Workbook(output, {'in_memory': True})
    worksheet = workbook.add_worksheet()
    worksheet.set_column('A:U', 12)

    worksheet.write(0, 0, title)
    col = 1
    row = 1

    order_items.sort(key=lambda SmsOrderItem: SmsOrderItem.amount, reverse=True)

    for order_item in order_items:
        worksheet.write(row, col, order_item.name_cn)
        col+=1

    for customer in customers:
        col = 0
        row+=1
        worksheet.write(row, col, customer.name_cn)
        customer_key = customer.key.urlsafe()

        if customer_key not in customer_order_map:
            continue

        order_map = customer_order_map[customer_key]
        for order_item in order_items:
            col+=1
            if order_item.product_id in order_map:
                worksheet.write(row, col, order_map[order_item.product_id])

    row+=1
    col = 0
    worksheet.write(row, col, 'Total')

    for order_item in order_items:
        col+=1
        worksheet.write(row, col, order_item.amount)

    workbook.close()
    return output.getvalue()

SYSTEM_HEAD_COLUMNS = ['so_no', 'so_date', 'invo_no', 'invo_date', 'rec_tag', 'cust_id',
                  'drv_no', 'term', 'tot_amt', 'net_amt', 'gst_amt', 'salesman', 'edit_date',
                  'bill_date1', 'bill_date2', 'user_id', 'prt_tag', 'gst_tag']
SYSTEM_DETAIL_COLUMNS = ['so_no', 'so_date', 'invo_no', 'invo_date', 'cust_id', 'drv_no',
                  'rec_no', 'rec_tag', 'item_id', 'descript', 'descript1', 'qty', 'unit',
                  'price', 'amt', 'bal_tag', 'salesman', 'edit_date', 'user_id']
MONTH_MAP = {
        1: 'Jan',
        2: 'Feb',
        3: 'Mar',
        4: 'Apr',
        5: 'May',
        6: 'Jun',
        7: 'Jul',
        8: 'Aug',
        9: 'Sep',
        10: 'Oct',
        11: 'Nov',
        12: 'Dec'
}

def pythonDateToExcelDate(date):
    return date.strftime('%d') + '-' + MONTH_MAP[date.month] + '-' + date.strftime('%y')

def getTotalAmount(order_item_map):
    amount = 0
    for key in order_item_map.keys():
        amount+=order_item_map[key].amount
    return amount

def generateSystemReportHead(ordermap):
    output = StringIO.StringIO()

    workbook = xlsxwriter.Workbook(output, {'in_memory': True})
    worksheet = workbook.add_worksheet()
    worksheet.set_column('A:U', 12)

    col = 0
    row = 0

    for column in SYSTEM_HEAD_COLUMNS:
        worksheet.write(row, col, column)
        col+=1

    days = ordermap.keys()
    days.sort()
    for day in days:
        customer_id_map = ordermap[day]
        customer_ids = customer_id_map.keys()
        customer_ids.sort()
        for customer_id in customer_ids:
            row+=1
            total_amount = getTotalAmount(customer_id_map[customer_id])
            target_date = pythonDateToExcelDate(day)
            worksheet.write(row, 0, target_date + '/' + customer_id)
            worksheet.write(row, 1, target_date)
            worksheet.write(row, 3, '  -   -')
            worksheet.write(row, 4, 'K')
            worksheet.write(row, 5, customer_id)
            worksheet.write(row, 8, total_amount)
            worksheet.write(row, 9, total_amount)
            worksheet.write(row, 12, target_date)
            worksheet.write(row, 13, '  -   -')
            worksheet.write(row, 14, '  -   -')
            worksheet.write(row, 15, 'HH03')
            worksheet.write(row, 16, 'Y')
            worksheet.write(row, 17, 1)

    workbook.close()
    return output.getvalue()

def generateSystemReportDetail(product_map, orderMap):

    output = StringIO.StringIO()

    workbook = xlsxwriter.Workbook(output, {'in_memory': True})
    worksheet = workbook.add_worksheet()
    worksheet.set_column('A:U', 12)

    col = 0
    row = 0

    for column in SYSTEM_DETAIL_COLUMNS:
        worksheet.write(row, col, column)
        col+=1

    days = orderMap.keys()
    days.sort()
    for day in days:
        customer_id_map = orderMap[day]
        customer_ids = customer_id_map.keys()
        customer_ids.sort()
        for customer_id in customer_ids:
            order_item_map = customer_id_map[customer_id]
            order_item_keys = order_item_map.keys()
            order_item_keys.sort()
            for index, order_item_key in enumerate(order_item_keys):
                row+=1

                order_item = order_item_map[order_item_key]
                target_date = pythonDateToExcelDate(day)
                worksheet.write(row, 0, target_date + '/' + customer_id)
                worksheet.write(row, 1, target_date)
                worksheet.write(row, 3, '  -   -')
                worksheet.write(row, 4, customer_id)
                worksheet.write(row, 6, index)
                worksheet.write(row, 8, product_map[order_item.product_id].hupheng_id)
                worksheet.write(row, 9, order_item.name_cn)
                worksheet.write(row, 10, order_item.name_en)
                worksheet.write(row, 11, order_item.amount)
                worksheet.write(row, 12, order_item.unit_name)
                worksheet.write(row, 15, '-')
                worksheet.write(row, 17, target_date)
                worksheet.write(row, 18, 'HH03')

    workbook.close()
    return output.getvalue()