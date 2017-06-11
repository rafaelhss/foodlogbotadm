(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .controller('ScheduledMealController', ScheduledMealController);

    ScheduledMealController.$inject = ['ScheduledMeal'];

    function ScheduledMealController(ScheduledMeal) {

        var vm = this;

        vm.scheduledMeals = [];

        loadAll();

        function loadAll() {
            ScheduledMeal.query(function(result) {
                vm.scheduledMeals = result;
                vm.searchQuery = null;
            });
        }
    }
})();
