(function() {
    'use strict';

    angular
        .module('foodlogbotadmApp')
        .controller('MealLogDeleteController',MealLogDeleteController);

    MealLogDeleteController.$inject = ['$uibModalInstance', 'entity', 'MealLog'];

    function MealLogDeleteController($uibModalInstance, entity, MealLog) {
        var vm = this;

        vm.mealLog = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            MealLog.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
