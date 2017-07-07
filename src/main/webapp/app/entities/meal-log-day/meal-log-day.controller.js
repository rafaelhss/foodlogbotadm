(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .controller('MealLogDayController', MealLogDayController);

    MealLogDayController.$inject = ['DataUtils', 'MealLogDay', 'ParseLinks', 'AlertService', 'paginationConstants', 'VisDataSet', '$scope'];

    function MealLogDayController(DataUtils, MealLogDay, ParseLinks, AlertService, paginationConstants, VisDataSet, $scope) {

        var vm = this;

        vm.openFile = DataUtils.openFile;


        vm.mealLogDays = [];
        vm.loadPage = loadPage;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.page = 0;
        vm.links = {
            last: 0
        };
        vm.predicate = 'id';
        vm.reset = reset;
        vm.reverse = true;

        loadAll();

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
                for (var i = 0; i < data.length; i++) {
                    vm.mealLogDays.push(data[i]);
                    var timelineData = {};
                    timelineData.items = extractItens(data[i]);
                    $scope.timelineDatas.push(timelineData);
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
