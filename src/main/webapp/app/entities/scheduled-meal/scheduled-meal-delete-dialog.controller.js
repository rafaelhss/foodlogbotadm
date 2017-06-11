(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .controller('ScheduledMealDeleteController',ScheduledMealDeleteController);

    ScheduledMealDeleteController.$inject = ['$uibModalInstance', 'entity', 'ScheduledMeal'];

    function ScheduledMealDeleteController($uibModalInstance, entity, ScheduledMeal) {
        var vm = this;

        vm.scheduledMeal = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ScheduledMeal.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
