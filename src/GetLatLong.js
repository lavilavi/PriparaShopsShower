/**
 * Created by mizutani on 2016/10/21.
 */



function getLatLng(place) {

    // ジオコーダのコンストラクタ
    var geocoder = new google.maps.Geocoder();

    // geocodeリクエストを実行。
    // 第１引数はGeocoderRequest。住所⇒緯度経度座標の変換時はaddressプロパティを入れればOK。
    // 第２引数はコールバック関数。
    geocoder.geocode({
        address: place
    }, function(results, status) {
        if (status == google.maps.GeocoderStatus.OK) {

            // 結果の表示範囲。結果が１つとは限らないので、LatLngBoundsで用意。
            var bounds = new google.maps.LatLngBounds();

            for (var i in results) {
                if (results[i].geometry) {

                    // 緯度経度を取得
                    var latlng = results[i].geometry.location;

                    // 住所を取得(日本の場合だけ「日本, 」を削除)
                    var address = results[i].formatted_address.replace(/^日本, /, '');

                    // 検索結果地が含まれるように範囲を拡大
                    bounds.extend(latlng);

                    // あとはご自由に・・・。
                    new google.maps.InfoWindow({
                        content: address + "<br>(Lat, Lng) = " + latlng.toString()
                    }).open(map, new google.maps.Marker({
                        position: latlng,
                        map: map
                    }));
                }
            }

            // 範囲を移動
            map.fitBounds(bounds);

        } else if (status == google.maps.GeocoderStatus.ERROR) {
            alert("サーバとの通信時に何らかのエラーが発生！");
        } else if (status == google.maps.GeocoderStatus.INVALID_REQUEST) {
            alert("リクエストに問題アリ！geocode()に渡すGeocoderRequestを確認せよ！！");
        } else if (status == google.maps.GeocoderStatus.OVER_QUERY_LIMIT) {
            alert("短時間にクエリを送りすぎ！落ち着いて！！");
        } else if (status == google.maps.GeocoderStatus.REQUEST_DENIED) {
            alert("このページではジオコーダの利用が許可されていない！・・・なぜ！？");
        } else if (status == google.maps.GeocoderStatus.UNKNOWN_ERROR) {
            alert("サーバ側でなんらかのトラブルが発生した模様。再挑戦されたし。");
        } else if (status == google.maps.GeocoderStatus.ZERO_RESULTS) {
            alert("見つかりません");
        } else {
            alert("えぇ～っと・・、バージョンアップ？");
        }
    });
}