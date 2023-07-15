/*
 * @Author: xx
 * @Date: 2023-07-15 18:58:53
 * @LastEditTime: 2023-07-15 19:50:52
 * @Description: 
 */
javascript:
window.jsBridge.postMessage = function(eventName, params) {
    var obj = {}
    obj.currency = 'USD'
    obj.amount = -1.0
    if (eventName === 'firstrecharge' || eventName === 'recharge' || eventName === 'withdrawOrderSuccess') {
        var arr = JSON.parse(params)
        obj.amount = arr.amount
        obj.currency = arr.currency
    }
    obj.method = 'event'
    obj.eventType = 'af'
    obj.eventName = eventName
    obj.param = JSON.stringify(params)

    window.androidJs.onCall(JSON.stringify(obj)) 
}