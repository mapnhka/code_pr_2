require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /

    state: Start
        q!: $regex</start>
        a: Список команд:\nadd - для добавления данных в корзину\nclear - очистка корзины\npay - создать счет\nstart - оплатить\ncheck - проверить статус платежа\nshow - показать данные в корзине
    
    state:
        q!: * запусти *
        a: введите текст с ssml
        
        state: new_1
            event: noMatch
            script:
                var reply = {
                    "type":"text",
                    "text": '<speak>Сбербанк <break time="4000ms"/> самый лучший</speak>',
                    "ssml": '<speak>Сбербанк <break time="4000ms"/> самый лучший</speak>'
                };
                $response.replies = $response.replies || [];
                $response.replies.push(reply);
                
    state:
        q!: ssml
        a: введите текст с ssml re
        
        state:
            event: noMatch
            script:
                var reply = {
                    "type":"text",
                    "text": $parseTree.text,
                    "ssml": $parseTree.text
                };
                $response.replies = $response.replies || [];
                $response.replies.push(reply);
            
    state: testState
        q!: old
        a: введите текст
        
        state: new_1
            event: noMatch
            script:
                var reply = {
                    "type":"raw",
                    "body": {
                        "items": [
                            {
                                "bubble": {
                                    "text": $parseTree.text
                                }
                            }
                        ],
                        "pronounceText": $parseTree.text,
                        "pronounceTextType": "application/ssml"
                    }
                };
                $response.replies = $response.replies || [];
                $response.replies.push(reply);
                
    state:
        q!: просто текст
        a: это просто текст
                
    state: addItem
        q!: add
        a: Добавляю в корзину!
        script:
            $payment.addItem({
                "position_id": 1,
                "name": "Кучка из 100 кристаллов для использования при нырянии",
                "item_params": [
                  {
                    "key": "packageName",
                    "value": "com.MashaAndTheBear.HairSalon"
                  }
                ],
                "quantity": {
                  "value": 1,
                  "measure": "кг."
                },
                "item_amount": 11836,
                "currency": "RUB",
                "item_code": "com.MashaAndTheBear.HairSalon.crystal100",
                "item_price": 11836,
                "discount_type": "percent",
                "discount_value": 5.25,
                "interest_type": "agentPercent",
                "interest_value": 15.105,
                "tax_type": 6,
                "tax_sum": 2367,
                "image": "https://i-love-png.com/images/grim-reaper-icon.png"
              })
              
    state: 
        q!: pay
        a: Вы сказали: {{$parseTree.text}}
        script:
            var response = $payment.createPayment({
              "purchaser": {
                "email": "qq@dd.eof",
                "phone": 9123456789,
                "contact": "email"
              },
              "delivery_info": {
                "address": {
                  "country": "RU",
                  "city": "Москва",
                  "address": "ул. Вавилова, 19, офис 1"
                },
                "delivery_type": "courier",
                "description": "Перезвонить за 1,5 часа"
              },
              "order": {
                "order_id": "d290f1ee-6c54-4b01-90e6-d701748f0851",
                "order_number": 145,
                "service_id": "9",
                "amount": 11836,
                "currency": "RUB",
                "purpose": "Покупка в игре \"Маша и Медведь, салон красоты Чародейка\".",
                "description": "Покупка внутриигрового контента в игре Маша и Медведь, салон красоты Чародейка.",
                "language": "ru-RU",
                "expiration_date": "2020-07-31T07:32:56.288Z",
                "autocompletion_date": "2020-07-31T07:32:56.288Z",
                "tax_system": 0
              }
            });
            $session.invoice_id = response.invoice_id
            log('!!!!!!!!!!!!!' + $session.invoice_id)
              
    state: clearItems
        q!: clear
        a: Очищаю корзину
        script:
            $payment.clearItems()
        
    state:
        q!: show
        a: {{ toPrettyString($session.smartMarketBucket) }}
        script:
            log(JSON.stringify($payment.getItems()))
            log(JSON.stringify($context))
            
    state:
        q!: check
        script:
            log($payment.checkPayment(5555))
            // $session.invoice_id
            
    state:
        q!: event
        script:
            $reactions.pay($session.invoice_id)
            
    state:
        q!: id
        a: {{ $session.invoice_id }}
            
    state:
        event!: POLICY_RUN_APP
        a: Операция произведена

    state: Fallback
        event!: noMatch
        a: Я не понял. Вы сказали: {{$request.query}}

