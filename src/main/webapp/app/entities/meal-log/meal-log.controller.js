(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .controller('MealLogController', MealLogController);

    MealLogController.$inject = ['DataUtils', 'MealLog', 'ParseLinks', 'AlertService', 'paginationConstants'];

    function MealLogController(DataUtils, MealLog, ParseLinks, AlertService, paginationConstants) {

        var vm = this;

        String.prototype.toHHMMSS = function () {
            var sec_num = parseInt(this, 10); // don't forget the second param
            var hours   = Math.floor(sec_num / 3600);
            var minutes = Math.floor((sec_num - (hours * 3600)) / 60);
            var seconds = sec_num - (hours * 3600) - (minutes * 60);

            if (hours   < 10) {hours   = "0"+hours;}
            if (minutes < 10) {minutes = "0"+minutes;}
            if (seconds < 10) {seconds = "0"+seconds;}
            return hours+'h:'+minutes+'m';//:'+seconds;
        }

        vm.getTimeBarHeight = function(date1, date2){
            return '{"height":"'+(new Date(date1) - new Date(date2))/ (1000 * 60)+'px"}';
        }

        vm.getTimeInMinutes = function (date1, date2){
          var offset = (new Date(date1) - new Date(date2))/ (1000);
          offset = Math.floor(offset).toString();
          return offset.toHHMMSS();
        }

        vm.mealLogs = [];
        vm.loadPage = loadPage;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.page = 0;
        vm.links = {
            last: 0
        };
        vm.predicate = 'id';
        vm.reset = reset;
        vm.reverse = true;
        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;

        loadAll();

        function loadAll () {
            MealLog.query({
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

            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                for (var i = 0; i < data.length; i++) {
                    vm.mealLogs.push(data[i]);
                }
            }

            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function reset () {
            vm.page = 0;
            vm.mealLogs = [];
            loadAll();
        }

        function loadPage(page) {
            vm.page = page;
            loadAll();
        }
    }
})();
