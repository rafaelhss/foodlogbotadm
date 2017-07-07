(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .controller('MealLogDayDeleteController',MealLogDayDeleteController);

    MealLogDayDeleteController.$inject = ['$uibModalInstance', 'entity', 'MealLogDay'];

    function MealLogDayDeleteController($uibModalInstance, entity, MealLogDay) {
        var vm = this;

        vm.mealLogDay = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            MealLogDay.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
