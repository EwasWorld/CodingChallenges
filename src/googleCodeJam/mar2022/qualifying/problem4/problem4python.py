import time
from os.path import exists
from abc import ABC, abstractmethod


class IoHandler(ABC):
    @abstractmethod
    def next_input_line(self):
        pass

    def next_expected_output_line(self):
        """
        :raises NotImplementedError: this function is not supported with this IoHandler
        """
        raise NotImplementedError("Function not supported: next_expected_output_line")

    def can_check_output(self):
        """
        :return: False (expected output is not supported with this IoHandler)
        """
        return False


class FileIoHandler(IoHandler):
    """
    Loads inputs and expected_outputs from the file system
    """

    def __init__(self, test_set):
        """
        :param test_set: int: 0 for the sample test set, 1-3 for main test sets

        :raises ValueError: if test_set is out of range
        :raises FileNotFoundError: if the input file cannot be found
        """

        # Construct file paths
        path_prefix = "T:\\Dropbox\\CodingMisc\\CodingChallenges\\src\\googleCodeJam\\mar2022\\qualifying\\problem4\\"
        if test_set == 0:
            file_name = "%ssample_test_set_1\\sample_ts1" % path_prefix
        elif test_set in range(1, 4):
            file_name = "%stest_set_%d\\ts%d" % (path_prefix, test_set, test_set)
        else:
            raise ValueError("Invalid test set: %d. Must be between 0 and 3 (inclusive)" % test_set)

        # Resolve input file
        input_file_name = "%s_input.txt" % file_name
        if not exists(input_file_name):
            raise FileNotFoundError("Input file does not exist: %s" % input_file_name)
        self.input_file = open(input_file_name, 'r')

        # Resolve output file
        expected_output_file_name = "%s_output.txt" % file_name
        if not exists(expected_output_file_name):
            print("Warning: Input file does not exist: %s. Continuing without validation" % expected_output_file_name)
        else:
            self.expected_output_file = open(expected_output_file_name, 'r')

    def next_input_line(self):
        """
        :return: string: a line from the input file
        """

        return self.input_file.readline()

    def next_expected_output_line(self):
        """
        :return: string: a line from the expected output file
        """

        try:
            return self.expected_output_file.readline().strip("\n")
        except AttributeError:
            raise FileNotFoundError("No expected output file provided")

    def can_check_output(self):
        """
        :return: boolean: true if there is a valid output file
        """

        try:
            return self.expected_output_file.readable()
        except AttributeError:
            return False


# PyCharm doesn't understand that `raise` terminates `next_expected_output_line`
# noinspection PyAbstractClass
class ConsoleIoHandler(IoHandler):
    """
    Reads input from the console. Does not check output
    """

    def next_input_line(self):
        """
        :return: string: a line read from the console
        """
        return input()


def process_sub_tree_for_node(current_node_index, fun_factors_pstfn, next_nodes_grouped_pstfn):
    """
    RECURSIVE

    Process the current_node_index and all nodes that point to it

    :param current_node_index: int: index in fun_factors_pstfn and next_nodes_grouped_pstfn to process
    :param fun_factors_pstfn: [int] fun factors of all nodes
    :param next_nodes_grouped_pstfn: [[int]] `next_nodes_grouped[i] = [x, y, z]`
            - node i is pointed to by nodes x, y, and z
    :return: Tuple(offshoots_sum: int, current_node_effective_fun: int):
       - offshoots_sum: the sum of any offshoot branches that have terminated
       - current_node_effective_fun: The single fun factor that should be used in place of this subtree when processing
                nodes closer to the root
    """

    current_node_fun = fun_factors_pstfn[current_node_index]
    # Indexes of all nodes immediately before this one
    previous_nodes_indexes = next_nodes_grouped_pstfn[current_node_index]
    if len(previous_nodes_indexes) == 0:
        return 0, current_node_fun

    offshoots_sum = 0
    # The effective fun factor of the nodes that point to the current node
    immediately_previous_nodes_fun = []
    for previous_index in previous_nodes_indexes:
        previous_offshoots_sum, previous_node_effective_fun = process_sub_tree_for_node(
            previous_index, fun_factors_pstfn, next_nodes_grouped_pstfn
        )
        offshoots_sum += previous_offshoots_sum
        immediately_previous_nodes_fun.append(previous_node_effective_fun)

    # Remove the lowest value as this will be overridden by whatever the current node becomes
    #   or will be used as the current node fun therefore shouldn't be added to the total
    immediately_previous_nodes_fun.sort()
    current_node_effective_fun = immediately_previous_nodes_fun.pop(0)
    if current_node_effective_fun <= current_node_fun:
        current_node_effective_fun = current_node_fun

    # All remaining previous nodes' branches are effectively terminated,
    #   either succeeded by the lowest or by current_node_fun
    offshoots_sum += sum(immediately_previous_nodes_fun)

    return offshoots_sum, current_node_effective_fun


"""
Set up inputs
"""
file_input_test_set = 3
use_console_input_type = False
print_debug_logs = True

if use_console_input_type:
    io_handler = ConsoleIoHandler()
else:
    io_handler = FileIoHandler(file_input_test_set)

"""
Process test cases
"""
start_time = time.time()
test_cases = int(io_handler.next_input_line())
for test_case_index in range(test_cases):
    input_size = int(io_handler.next_input_line())
    # `fun_factors[i] = x` - node i has fun factor x
    fun_factors = [int(fun_factor) for fun_factor in io_handler.next_input_line().split(" ")]
    # `next_nodes[i] = j` - node i points to node j
    next_nodes = [int(next_node) - 1 for next_node in io_handler.next_input_line().split(" ")]
    if io_handler.can_check_output():
        expected_output = io_handler.next_expected_output_line()
    else:
        expected_output = None

    """
    Validate test case inputs
    """
    if not use_console_input_type and file_input_test_set == 2 and test_case_index == 88:
        continue
    if not use_console_input_type and file_input_test_set == 3 and test_case_index == 80:
        continue
    if len(fun_factors) != input_size:
        raise RuntimeError(
            "Unexpected list size - fun_factors. Expected %d, Actual %d" % (input_size, len(fun_factors))
        )
    if len(next_nodes) != input_size:
        raise RuntimeError("Unexpected list size - next_nodes. Expected %d, Actual %d" % (input_size, len(fun_factors)))

    """
    Group next_nodes
    """
    # `next_nodes_grouped[i] = [x, y, z]` - node i is pointed to by nodes x, y, and z
    next_nodes_grouped = [[] for _ in range(input_size)]
    # nodes that point to nothing - i.e. the root of each subtree
    terminating_nodes = []
    for node_index in range(input_size):
        next_node_index = next_nodes[node_index]
        if next_node_index >= 0:
            next_nodes_grouped[next_node_index].append(node_index)
        elif next_node_index == -1:
            terminating_nodes.append(node_index)
        else:
            raise RuntimeError("Unexpected next_node_index: %d" % next_node_index)

    """
    Process each sub-tree
    """
    final_sum = 0
    for node_index in terminating_nodes:
        node_sum, fun_brought_forward = process_sub_tree_for_node(node_index, fun_factors, next_nodes_grouped)
        final_sum += node_sum
        final_sum += fun_brought_forward

    """
    Construct and calculate output
    """
    out_string = "Case #%d: %d" % (test_case_index + 1, final_sum)
    if io_handler.can_check_output() and out_string != expected_output:
        raise RuntimeError("%s - Wrong answer" % out_string)
    print(out_string)

if print_debug_logs:
    print("--- %s seconds ---" % round(time.time() - start_time, 2))
