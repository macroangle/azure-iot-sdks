angular.module('iPOSModule', ['counter'])
    .filter('rupeeFilter', ['$filter', function($filter){
        return function(item){
            return $filter('currency')(item, '\u20B9 ');
        };
    }])

    .factory('socket', ['$rootScope', function ($rootScope) {
        var socket = io.connect('http://localhost:3000');
        return {
            on: function (eventName, callback) {
              socket.on(eventName, function () {
                var args = arguments;
                $rootScope.$apply(function () {
                  callback.apply(socket, args);
                });
              });
            },
            emit: function (eventName, data, callback) {
              socket.emit(eventName, data, function () {
                var args = arguments;
                $rootScope.$apply(function () {
                  if (callback) {
                    callback.apply(socket, args);
                  }
                });
              });
            }
        };
    }])

    .directive('gChart',['$filter','$timeout', function ($filter, $timeout){
       return {
            restrict: 'A',
            replace: true,
            template: '<div></div>',
            link: function ($scope, elm, attr) {
                //Initialize section height as per content
                var sectionHeight = $(window).height();
                angular.element(elm).height(sectionHeight);
                $('#chart_stats').height(sectionHeight);

                //Initialize google Map
                var map = new google.maps.Map(document.getElementById(attr.id), {
                    center: new google.maps.LatLng(12,77),
                    zoom: 7,
                    mapTypeId: google.maps.MapTypeId.ROADMAP,
                });

                $scope.$watch(attr.gChart, function(newVal, oldVal){
                    if(angular.isDefined(newVal)){
                        angular.forEach($scope.chart, function(position, index){
                            //Mark Points
                            if(!position.plotter){
                                var contentString = '<h2>Transaction Details</h2>';
                                angular.forEach(position.items, function(item, index){
                                    contentString += item.name + ' : ' + $filter('rupeeFilter')(item.subtotal) + '<br>';
                                });
                                position.plotted = true;
                                var marker = new google.maps.Marker({
                                    position: new google.maps.LatLng(position.lat, position.lng),
                                    map: map,
                                    icon: 'http://icons.iconarchive.com/icons/custom-icon-design/flatastic-9/24/Pin-red-icon.png'
                                });
                                if(position.items){
                                    var infowindow = new google.maps.InfoWindow({
                                                  content: contentString
                                                });
                                    marker.addListener('click', function() {
                                        infowindow.open(map, marker);
                                    });
                                }
                                if(position.add){
                                    marker.animation = google.maps.Animation.DROP;
                                    marker.icon = 'http://checker-website-assets.s3-website-us-east-1.amazonaws.com/images/star-writeareview-active.png';
                                    marker.setMap(map);
                                }else{
                                    marker.setMap(map);
                                }
                            }
                        });
                       $timeout(function() {
                           $scope.loading = false;
                        },2000);
                    }
                },true);
            }
        };
      }])

      .controller('homeCtrl', ['$scope', 'socket', function ($scope, socket) {
            $scope.ctrl = {
                myValue: 1000,
                myTarget: 2000,
                myDuration: 5000
            };
            $scope.totalByItems = {};
            $scope.totalByCategories = {};
            $scope.grossTotal = 0;
            $scope.grossTotalNew = 0;
            $scope.loading = true;
            var chartData = [];
            var updateStatistics = function(itemData, newPoint){
                if(newPoint){
                    $scope.grossTotalNew = $scope.grossTotal + (itemData.total || 0);
                }else{
                    $scope.grossTotal += (itemData.total || 0);
                }
                chartData.push({
                    lng: itemData.lng,
                    lat: itemData.lat,
                    total: itemData.total,
                    marker: 'blue',
                    items: itemData.items,
                    add: newPoint
                });

                angular.forEach(itemData.items, function(transactionItem, counter){
                    //Aggregate items
                    $scope.totalByItems[transactionItem.name] = $scope.totalByItems[transactionItem.name] || 0;
                    $scope.totalByItems[transactionItem.name] += transactionItem.subtotal;

                    //Aggregate Categories
                    $scope.totalByCategories[transactionItem.category] = $scope.totalByCategories[transactionItem.category] || 0;
                    $scope.totalByCategories[transactionItem.category] += transactionItem.subtotal;
                });
            };

            socket.on('initial messages', function (data) {
                angular.forEach(data, function(item, index){
                    if(item.message){
                        updateStatistics(JSON.parse(item.message.replace(/'/g, '"')));
                    }
                });
                $scope.chart = chartData;
            });

            socket.on('new message', function (item) {
                item.message = JSON.parse(item.message.replace(/'/g, '"'));
                if(item.message){
                    updateStatistics(item.message, true);
                }
                $scope.chart = chartData;
            });
        }]
    );