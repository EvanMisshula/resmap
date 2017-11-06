(ns resmap.views
  (:require [re-frame.core :as re-frame]
            [reagent.core :as reagent]))


(defn loading-throbber
  []
  (let [loading? (re-frame/subscribe [:loading?])]
    (when @loading?
      [:div.loading
       [:div.three-quarters-loader "Loading..."]])))

(defn service-name-input
  []
  (let [loading? (re-frame/subscribe [:loading?])
        error? (re-frame/subscribe [:error?])
        service-name (reagent/atom "")
        on-click (fn [_]
                   (when-not (empty? @service-name)
                     (re-frame/dispatch [:set-service-name @service-name])
                     (reset! service-name "")))]
    (fn []
      [:div
       [:div.input-group
        [:input.form-control {:type "text"
                              :placeholder "Enter Service-Name"
                              :on-change #(reset! service-name (-> % .-target .-value))}]
        [:span.input-group-btn
         [:button.btn.btn-default {:type "button"
                                   :on-click #(when-not @loading? (on-click %))}
          "Submit"]
         ]]
       (when @error?
         [:p.error-text.text-danger "¯\\_(ツ)_/¯  Bad service name!"])])))

(defn service-name-and-avatar
  []
  (fn []
    (let [service-profile (re-frame/subscribe [:service-profile])]
      [:div.user-details
       [:img.img-circle {:src (get (first @service-profile) "avatarurl")}]
       [:h5.text-center (get (first @service-profile) "serviceid")]])))
 
(defn service-sites-list
  []
  (let [service-sites (re-frame/subscribe [:service-sites])]
    (fn []
      [:ul.list-group
       (map-indexed (fn [i site]
                      (vector :li.list-group-item {:key i}
                              [:h4.list-group-item-heading (get site "name")]
                              [:p.list-group-item-text (get site "service")]
                              [:p.list-group-item-text (str "lon: " (get site "lon")
                                                            ", lat: "
                                                           (get site "lat") )]
                              ))
                    @service-sites)])))
;; home
(defn mapCanvas []
  (fn []
    [:div {:style {:width "700px" :height "300px" :background-color "green"} }]))

(defn home-panel []
  (let []
    (fn []
      [:div
       [:div.topbar
        [:div.container
         [:div.row
          [:div.col-lg-4.col-lg-offset-1
           [service-name-input]]]]]
       [:div.main-content
        [:div.container
         [:div.row
          [:div.col-lg-4.col-lg-offset-1
           [service-sites-list]
           [mapCanvas]
           ]]]]
       ])))


;; profile

(defn profile-panel []
  (fn []
    [:div "This is the Profile Page."
     [:div [:a {:href "#/"} "go to Home Page"]]
     ]))


;; main

(defmulti panels identity)
(defmethod panels :home-panel [] [home-panel])
(defmethod panels :profile-panel [] [profile-panel])
(defmethod panels :default [] [:div])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      [:div
       [loading-throbber]
       [service-name-and-avatar]
       (panels @active-panel)
       ])))
