(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .controller('MealLogDayController', MealLogDayController);

    MealLogDayController.$inject = ['DataUtils', 'MealLogDay', 'ParseLinks', 'AlertService', 'paginationConstants', 'VisDataSet', '$scope', '$http', '$filter'];

    function MealLogDayController(DataUtils, MealLogDay, ParseLinks, AlertService, paginationConstants, VisDataSet, $scope, $http, $filter) {

        var vm = this;

        vm.openFile = DataUtils.openFile;


        vm.mealLogDays = [];
        vm.loadPage = loadPage;
        vm.itemsPerPage = paginationConstants.itemsPerPage * 3;
        vm.page = 0;
        vm.links = {
            last: 0
        };
        vm.predicate = 'id';
        vm.reset = reset;
        vm.reverse = true;


        var scheduledMeals = [];

        //primeiro busca os scheduled para fazer os background, depois busca os dias.
        $http.get("/api/scheduled-meals").then(function(data){
                scheduledMeals = data.data;
                loadAll();
            } , function(error){
                console.log(error)
            });



        function loadAll () {
            MealLogDay.query({
                page: vm.page,
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
                buildTimelineInfo(data);
            }

            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function reset () {
            vm.page = 0;
            vm.mealLogDays = [];
            loadAll();
        }

        function loadPage(page) {
            vm.page = page;
            loadAll();
        }
    }
})();
