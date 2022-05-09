(ns user.hytradboi
  (:require [hyperfiddle.api :as hf]
            [hyperfiddle.photon :as p]
            [hyperfiddle.rcf :refer [tests ! %]]
            [hyperfiddle.ui.codemirror :as codemirror]
            [hyperfiddle.ui :as ui]
            [missionary.core :as m]
            [user.orders :refer [orders genders shirt-sizes]]
            dustin.y2022.edn-render))

(p/defn App []
  (binding [hf/render ui/render] ; default to dom so issues show up in test
    (hf/hfql

      {(orders .)
       [:order/email
        {(props :order/gender {::hf/options (genders)})
         [:db/ident]}
        {(props :order/shirt-size {::hf/options (shirt-sizes order/gender .)})
         [:db/ident]}]}

      )))

(p/defn view []
  (new codemirror/edn
       ~@#_"server"
       (binding [hf/db hf/*db*]
         (ui/with-spec-render (App.)))))

(comment
  (def !x (atom "alice"))

  (p/run (! (new App (new (m/watch !x)))))

  % := '{(user.orders/orders _)
         [{:order/gender     #:db{:ident :order/female},
           :order/email      "alice@example.com",
           :order/shirt-size _}]}

  (reset! !x "bob")

  % := '{(user.orders/orders _)
         [{:order/gender     #:db{:ident :order/male},
           :order/email      "bob@example.com",
           :order/shirt-size _}]}
  )