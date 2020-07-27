需要配置jwk验证签名

google的jwk url
https://www.googleapis.com/oauth2/v3/certs

{
  "keys": [
    {
      "alg": "RS256",
      "e": "AQAB",
      "kid": "4e4ebe487d5cdf2b026a3b229d86f0d4258449fe",
      "n": "zCNE-FWFIM7ei_e8hqjW3MTndHJxpQ1aJEFuDjzXqlTkoF_mDgZIynUOzypUDtfWxN81-irNoCIYNQQam8zO_gkQr3aXmY6oXCdyXb24YD0oudxjtIGaKkPB9DfEZsKat6YA9MYFg3xklLAncmvksc8Tqu_uB1U3n1XcKUb12FhkUEUJVBZINo74aItEwsYx9ZyTkqEJnvL9YBja3dwSON0fm8DD_SeeFv7sRQhcQDUHYZj6jjiLfXEQzGmrbyzabGbVfzFZD0X7elGItXoJofb1UXadO_K_jVEXq_jtra7At2jJLYLwRFXG-HhRI7-qsPAdw8QnpeDy2ej8k9zCPw",
      "kty": "RSA",
      "use": "sig"
    },
    {
      "kty": "RSA",
      "kid": "b63ee0be093d9bc312d958c9966d21f0c8f6bbbb",
      "e": "AQAB",
      "n": "-AS7NlginBBXBMgB-ZWn9frB82HvW8jU7sMxk5Frhwc8LR1pJtdrl39yiBGO8Sa-YyuL56JD8rrWesHhLLp76rtW5Xpups_gbzJn3vnbG-d1-b0BEp9Drjd3eMsPzaGQl0mQCBTyhY_D11CINQ1LovLVR8RV7VjpRehkYjwmMrQPa0-I0K5LQi2stEZ-XV7_BPPqMq5g9O-g6O38suQPbZYykBL5J30YdiN9NsyXDObOX28jONsHdj7q2lvtcJDFjupUowPfgHEen_Pfq9ERaNkQ8zyqAPw9htpQ5U-9NDlFrPrBsWPzJ1MdwR-b0ISLAcHFaEfDYrEHalW3lf7Hew",
      "alg": "RS256",
      "use": "sig"
    }
  ]
}

