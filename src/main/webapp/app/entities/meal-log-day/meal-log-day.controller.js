(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .controller('MealLogDayController', MealLogDayController);

    MealLogDayController.$inject = ['$state', 'MealLogDay', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams', 'VisDataSet', '$scope', '$http'];

    function MealLogDayController($state, MealLogDay, ParseLinks, AlertService, paginationConstants, pagingParams, VisDataSet, $scope, $http) {

        var vm = this;

        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;

         var scheduledMeals = [];

        //primeiro busca os scheduled para fazer os background, depois busca os dias.
        $http.get("/api/scheduled-meals").then(function(data){
                scheduledMeals = data.data;
                loadAll();
            } , function(error){
                 AlertService.error(error.data.message);
                 console.log(error)
            });


        function loadAll () {
            MealLogDay.query({
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }

            //tudo de custom esta aqui, exceto injecao do $scope e open file
            function buildTimelineInfo(data) {
                $scope.timelineDatas = [];
                $scope.timelineOptions = {
                  //  height:"100%"
                };
                // Para cada dia recebido do backend. de traz pra frente pq o componente pega por ordem de entrada e queremos o maior em cima
                for (var i = data.length -1; i >=0 ; i--) {
                    vm.mealLogDays.push(data[i]);
                    var timelineData = {};

                    var items = new VisDataSet();

                    //busca os meallogs do dia
                    timelineData.items = extractItens(data[i], items);

                    //define os scheduled do dia
                    timelineData.items = extractBackgroundItens(scheduledMeals, data[i].mealLogDayDate, timelineData.items);

                    $scope.timelineDatas.push(timelineData);


                }

                function extractBackgroundItens(scheduledMeals, mealLogDayDate, items){
                    scheduledMeals.forEach(function(item,i){
                        var data = new Date(mealLogDayDate.toString().replace("00:00:", item.targetTime + ":"));
                        var start = new Date(data.getTime() - (20*60*1000) );
                        var end = new Date(data.getTime() + (20*60*1000) );
                                                items.add({
                                                    id: i+items.length,
                                                    content:
                                                    ' <span style="color:#97B0F8;">' +
                                                    //'<a  onClick="window.open(\'data:'+ item.photoContentType + ';base64,' + item.photo + '\')\">' +
                                                    item.name +
                                                    //    "<img src=\"data:" + item.photoContentType + ";base64," + item.photo + "\" style=\"max-height: 30px;\" alt=\"mealLog image\"/>" +
                                                    //'</a>' +
                                                     '</span>',
                                                    start: start,
                                                    end: end,
                                                    type: 'background'
                                                    });
                    });
                    return items;
                }

                function extractItens(data) {
                    var items = new VisDataSet();
                    data.mealLogList.forEach(function(item, i){
                        var start = new Date(item.mealDateTime);
                        items.add({
                            id: i,
                            content:
                            ' <span style="color:#97B0F8;">' +
                            '<a  onClick="window.open(\'data:'+ item.photoContentType + ';base64,' + item.photo + '\')\">' +
                                "<img src=\"data:" + item.photoContentType + ";base64," + item.photo + "\" style=\"max-height: 30px;\" alt=\"mealLog image\"/>" +
                            '</a>' +
                             '</span>',
                            start: start,

                            type: 'box'
                            });
                    })
                    return items;
                }
            }


            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.mealLogDays = data;
                vm.page = pagingParams.page;
                buildTimelineInfo(data);
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function loadPage(page) {
            vm.page = page;
            vm.transition();
        }

        function transition() {
            $state.transitionTo($state.$current, {
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                search: vm.currentSearch
            });
        }
    }
})();
